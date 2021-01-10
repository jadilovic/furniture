package com.avlija.furniture.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import com.avlija.furniture.form.SampleInputs;
import com.avlija.furniture.model.Element;
import com.avlija.furniture.model.ElementQuantity;
import com.avlija.furniture.model.Order;
import com.avlija.furniture.model.Pipeline;
import com.avlija.furniture.model.PipelineProduct;
import com.avlija.furniture.model.Product;
import com.avlija.furniture.model.ProductElement;
import com.avlija.furniture.model.ProductQuantity;
import com.avlija.furniture.repository.ElementQuantityRepository;
import com.avlija.furniture.repository.ElementRepository;
import com.avlija.furniture.repository.OrderRepository;
import com.avlija.furniture.repository.PipelineRepository;
import com.avlija.furniture.repository.ProductQuantityRepository;
import com.avlija.furniture.repository.ProductRepository;
import com.avlija.furniture.service.ProductIdComp;

import org.docx4j.dml.wordprocessingDrawing.Inline;
import org.docx4j.jaxb.Context;
import org.docx4j.model.table.TblFactory;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.BinaryPartAbstractImage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.BooleanDefaultTrue;
import org.docx4j.wml.Color;
import org.docx4j.wml.Drawing;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.R;
import org.docx4j.wml.RPr;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.Tc;
import org.docx4j.wml.Text;
import org.docx4j.wml.Tr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class OrderController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ElementRepository elementRepository;
    
    @Autowired
    private ElementQuantityRepository elementQuantityRepository;
 
    @Autowired
    private PipelineRepository pipelineRepository;

    @Autowired
    private ProductQuantityRepository productQuantityRepository;
    
    private static Order printOrder;
    // CREATE ORDER
    
    @RequestMapping(value= {"admin/createorder/{id}"}, method=RequestMethod.GET)
    public ModelAndView createOrder(@PathVariable(name = "id") Integer id) {
     ModelAndView model = new ModelAndView();
     Pipeline pipeline = pipelineRepository.findById(id).get();
     Set<Product> productsList = sortByProductId(pipeline.getProducts());
     List<ProductQuantity> productsQuantityList = getProductQuantityList(productsList, pipeline);
     SampleInputs sampleInputs = new SampleInputs();
     sampleInputs.setPipelineId(id);
     model.addObject("productsQuantityList", productsQuantityList);
     model.addObject("pipeline", pipeline);
     model.addObject("productsList", productsList);
     model.addObject("sampleInputs", sampleInputs);
     model.setViewName("admin/create_order");
     return model;
    }
    
    @RequestMapping(value= {"admin/createorder"}, method=RequestMethod.POST)
    public ModelAndView addedElementQuantity(@Valid SampleInputs sampleInputs) {
     ModelAndView model = new ModelAndView();
     Set <Product> productsList = new TreeSet<>();
     	Pipeline templatePipeline = pipelineRepository.findById(sampleInputs.getPipelineId()).get();
        productsList = sortByProductId(templatePipeline.getProducts());
        List<ProductQuantity> productsQuantityList = getProductQuantityList(productsList, templatePipeline);
        // CREATING ORDER JUST FOR DISPLAY BEFORE CONFIRMATION
        Order order = new Order(new Date(), sampleInputs.getWorkPosition(), templatePipeline, 
        						sampleInputs.getOrderComment(), sampleInputs.getOrderPackaging(), 
        						sampleInputs.getOrderPrep(), 0);
       // orderRepository.save(order);
       // sampleInputs.setOrderId(order.getId());
        
   	  model.addObject("msg", "Kreirani radni nalog spreman za potvrdu");
   	  model.setViewName("admin/confirm_order");
     model.addObject("order", order);
     model.addObject("productsList", productsList);
     model.addObject("productsQuantityList", productsQuantityList);
     model.addObject("sampleInputs", sampleInputs);
     //model.addObject("totals", totals);
     return model;
    }
    
    @RequestMapping(value= {"admin/confirmorder"}, method=RequestMethod.POST)
    public ModelAndView confirmOrder(@Valid SampleInputs sampleInputs) {
     ModelAndView model = new ModelAndView();
     Set <Product> products = new TreeSet<>();
     	// ONCE ORDER IS CONFIRMED ORDER IS MARKED IN THE DATABASE
     	// Order order = orderRepository.findById(sampleInputs.getOrderId()).get();
     Pipeline orderPipeline = pipelineRepository.findById(sampleInputs.getPipelineId()).get();
     Order createdOrder = new Order(new Date(), sampleInputs.getWorkPosition(), orderPipeline, 
    		 				sampleInputs.getOrderComment(), sampleInputs.getOrderPackaging(), 
    		 				sampleInputs.getOrderPrep(), 1);
     	orderRepository.save(createdOrder);
     	// ALSO PIPELINE USED TO CREATE THE ORDER IS NO MORE ACTIVE
     	// Pipeline orderPipeline = order.getPipeline();
        products = sortByProductId(orderPipeline.getProducts());
        orderPipeline.setActive(0);
        pipelineRepository.save(orderPipeline);
        
        createdOrder = orderRepository.findByPipeline(orderPipeline);
                
        List<ProductQuantity> productsQuantityList = getProductQuantityList(products, orderPipeline);
        List<Integer> totals = new ArrayList<>();

        for(Product product: products) {
        	PipelineProduct pipelineProduct = new PipelineProduct(orderPipeline.getId(), product.getId());
        	ProductQuantity productQuantity = productQuantityRepository.findById(pipelineProduct).get();
        	Set<Element> elements = product.getElements();
            List<ElementQuantity> elementsQuantityList = getElementQuantityList(elements, product);
            	for(ElementQuantity elementQuantity: elementsQuantityList) {
            		int totalElementQuantity = productQuantity.getQuantity() * elementQuantity.getQuantity();
            		totals.add(totalElementQuantity);
            		Element element = elementRepository.findById(elementQuantity.getProductElement().getElementId()).get();
            		int newElementQuantity = element.getQuantity() - totalElementQuantity;
            		element.setQuantity(newElementQuantity);
            		elementRepository.save(element);
            	}
        	}
        
   	  	model.addObject("msg", "Potvrđen radni nalog");
   	  	model.setViewName("admin/order_profile");
   	  	model.addObject("order", createdOrder);
   	  	model.addObject("productsList", products);
   	  	model.addObject("productsQuantityList", productsQuantityList);
   	  	model.addObject("totals", totals);
     return model;
    }

    // Order profile
    @RequestMapping(value= {"home/orderprofile/{id}"}, method=RequestMethod.GET)
    	public ModelAndView orderProfile(@PathVariable(name = "id") Long id) throws Exception {
     ModelAndView model = new ModelAndView();
     Set <Product> products = new TreeSet<>();
     Order order = orderRepository.findById(id).get();
     products = sortByProductId(order.getPipeline().getProducts());
        
        List<ProductQuantity> productsQuantityList = getProductQuantityList(products, order.getPipeline());
     
        WordprocessingMLPackage wordPackage = null;
		try {
			wordPackage = WordprocessingMLPackage.createPackage();
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        MainDocumentPart mainDocumentPart = wordPackage.getMainDocumentPart();
        mainDocumentPart.addStyledParagraphOfText("Title", "Hello World!");
        mainDocumentPart.addParagraphOfText("Welcome To Baeldung!");
        
        ObjectFactory factory = Context.getWmlObjectFactory();
        P p = factory.createP();
        R r = factory.createR();
        Text t = factory.createText();
        t.setValue("Green Welcome To Baeldung");
        r.getContent().add(t);
        p.getContent().add(r);
        
        RPr rpr = factory.createRPr();       
        BooleanDefaultTrue b = new BooleanDefaultTrue();
        rpr.setB(b);
        rpr.setI(b);
        rpr.setCaps(b);
        
        Color green = factory.createColor();
        green.setVal("green");
        rpr.setColor(green);
        r.setRPr(rpr);
        
        mainDocumentPart.getContent().add(p);
        
        File image = new File("Desert.jpg");
        byte[] fileContent = Files.readAllBytes(image.toPath());
        BinaryPartAbstractImage imagePart = BinaryPartAbstractImage.createImagePart(wordPackage, fileContent);
        Inline inline = imagePart.createImageInline("Baeldung Image (filename hint)", "Alt Text", 1, 2, false);
        P Imageparagraph = addImageToParagraph(inline);
        mainDocumentPart.getContent().add(Imageparagraph);
        
        int writableWidthTwips = wordPackage.getDocumentModel()
        		  .getSections().get(0).getPageDimensions().getWritableWidthTwips();
        int columnNumber = 3;
        Tbl tbl = TblFactory.createTable(3, 3, writableWidthTwips/columnNumber);     
        List<Object> rows = tbl.getContent();
        for (Object row : rows) {
        	Tr tr = (Tr) row;
        	List<Object> cells = tr.getContent();
        	for(Object cell : cells) {
        		Tc td = (Tc) cell;
        		td.getContent().add(p);
        		}
        	}
        
        mainDocumentPart.getContent().add(tbl);
        
        File exportFile = new File("welcome.docx");
		wordPackage.save(exportFile);
		printOrder = order;
        
   	  	model.addObject("msg", "Profil Radnog Naloga");
   	  	model.setViewName("admin/order_profile");
   	  	model.addObject("productsList", products);
   	  	model.addObject("productsQuantityList", productsQuantityList);
   	  	model.addObject("order", order);   
     return model;
    }
    
	@RequestMapping("home/{fileName:.+}")
	public void downloadCreatedOrderInWord(HttpServletRequest request, HttpServletResponse response,
			@PathVariable("fileName") String fileName) throws IOException {

		File file = new File(fileName);
		if (file.exists()) {

			//get the mimetype
			String mimeType = URLConnection.guessContentTypeFromName(file.getName());
			if (mimeType == null) {
				//unknown mimetype so set the mimetype to application/octet-stream
				mimeType = "application/octet-stream";
			}

			response.setContentType(mimeType);

			/**
			 * In a regular HTTP response, the Content-Disposition response header is a
			 * header indicating if the content is expected to be displayed inline in the
			 * browser, that is, as a Web page or as part of a Web page, or as an
			 * attachment, that is downloaded and saved locally.
			 * 
			 */

			/**
			 * Here we have mentioned it to show inline
			 */
			// response.setHeader("Content-Disposition", String.format("inline; filename=\"" + file.getName() + "\""));

			//Here we have mentioned it to show as attachment
			response.setHeader("Content-Disposition", String.format("attachment; filename=\"RN-" + printOrder.getId() + "-OD-" + printOrder.getCreated().toGMTString() + "\".docx"));

			response.setContentLength((int) file.length());

			InputStream inputStream = new BufferedInputStream(new FileInputStream(file));

			FileCopyUtils.copy(inputStream, response.getOutputStream());

		}
	}
    
    private List<ElementQuantity> getElementQuantityList(Set<Element> elementList, Product product) {
   	 List<ElementQuantity> elementQuantitiyList = new ArrayList<ElementQuantity>();
   	 for(Element element: elementList) {
   		 ElementQuantity elementQuantity;
   		 try {
   			 elementQuantity = elementQuantityRepository.findById(new ProductElement(product.getId(), element.getId())).get();
   		 } catch(Exception e) {
   			 elementQuantity = new ElementQuantity(new ProductElement(product.getId(), element.getId()), 0);
   			 // productQuantityRepository.save(productQuantity);
   		 }
   		 elementQuantitiyList.add(elementQuantity);
   	 }
   	return elementQuantitiyList;
   }


    // CREATE PRODUCT QUANTITY LIST FOR THE PIPELINE
    
    private List<ProductQuantity> getProductQuantityList(Set <Product> products, Pipeline pipeline) {
      	 List <ProductQuantity> productQuantitiyList = new ArrayList<ProductQuantity>();
       	 for(Product product: products) {
       		 ProductQuantity productQuantity;
       		 try {
       			 productQuantity = productQuantityRepository.findById(new PipelineProduct(pipeline.getId(), product.getId())).get();
       		 } catch(Exception e) {
       			 productQuantity = new ProductQuantity(new PipelineProduct(pipeline.getId(), product.getId()), 0, null, null);
       			 // productQuantityRepository.save(productQuantity);
       		 }
       		 productQuantitiyList.add(productQuantity);
       	 }
       	return productQuantitiyList;
	}
    
    // SORTING PRODUCTS IN THE PIPELINE BY PRODUCT ID
    private Set<Product> sortByProductId(Set<Product> products) {
		Set<Product> sortedProducts = new TreeSet<>(new ProductIdComp());
	     for(Product product: products) {
	    	 sortedProducts.add(product);
	     	}
		return sortedProducts;
	}
    
    private static P addImageToParagraph(Inline inline) {
        ObjectFactory factory = new ObjectFactory();
        P p = factory.createP();
        R r = factory.createR();
        p.getContent().add(r);
        Drawing drawing = factory.createDrawing();
        r.getContent().add(drawing);
        drawing.getAnchorOrInline().add(inline);
        return p;
    }
    
}