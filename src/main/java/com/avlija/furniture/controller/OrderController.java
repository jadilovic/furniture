package com.avlija.furniture.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URLConnection;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import com.avlija.furniture.form.OrderWordProduct;
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
import org.docx4j.model.datastorage.XPathEnhancerParser.predicate_return;
import org.docx4j.model.table.TblFactory;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.BinaryPartAbstractImage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.BooleanDefaultTrue;
import org.docx4j.wml.Br;
import org.docx4j.wml.Color;
import org.docx4j.wml.Drawing;
import org.docx4j.wml.HpsMeasure;
import org.docx4j.wml.Jc;
import org.docx4j.wml.JcEnumeration;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.PPr;
import org.docx4j.wml.PPrBase.TextAlignment;
import org.docx4j.wml.R;
import org.docx4j.wml.RPr;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.Tc;
import org.docx4j.wml.Text;
import org.docx4j.wml.Tr;
import org.docx4j.wml.U;
import org.docx4j.wml.UnderlineEnumeration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
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
    public ModelAndView confirmOrder(@Valid SampleInputs sampleInputs) throws Exception {
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
        
        printOrder = createdOrder;
        createWordDocument(createdOrder);
        
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
        
        printOrder = order;
        createWordDocument(order);
        
   	  	model.addObject("msg", "Profil Radnog Naloga");
   	  	model.setViewName("admin/order_profile");
   	  	model.addObject("productsList", products);
   	  	model.addObject("productsQuantityList", productsQuantityList);
   	  	model.addObject("order", order);   
     return model;
    }
    
    // DISPLAY STATUS OF ORDER BY PIPELINE - used in list_all_pipelines and list_of_pipelines
    @RequestMapping(value= {"home/orderpipeline/{id}"}, method=RequestMethod.GET)
	public ModelAndView orderProfileByPipeline(@PathVariable(name = "id") Integer id) {
 ModelAndView model = new ModelAndView();
 Set <Product> products = new TreeSet<>();
 Pipeline pipeline = pipelineRepository.findById(id).get();
 try {
	 Order order = orderRepository.findByPipeline(pipeline);
	 printOrder = order;
	 products = sortByProductId(order.getPipeline().getProducts());
	 List<ProductQuantity> productsQuantityList = getProductQuantityList(products, order.getPipeline());
	 model.addObject("msg", "Profil Radnog Naloga");
	 model.setViewName("admin/order_profile");
	 model.addObject("productsList", products);
	 model.addObject("productsQuantityList", productsQuantityList);
	 model.addObject("order", order);   
 } catch(Exception e) {
     model.addObject("err", "Radni nalog za odabranu listu proizovda još nije kreiran");     
     //List<Pipeline> pipelines = pipelineRepository.findAll(Sort.by("id").descending());
     List<Pipeline> pipelines = pipelineRepository.findFirst10ByActive(1, Sort.by("id").descending());
     SampleInputs sampleInputs = new SampleInputs();
     model.addObject("sampleInputs", sampleInputs);
     model.addObject("pipelines", pipelines);
     model.setViewName("home/list_of_pipelines");
 }
 return model;
}
    
    // SEARCH AND DISPLAY ORDERS BY ORDER ID
    @RequestMapping(value= {"home/ordersearchid"}, method=RequestMethod.POST)
    public ModelAndView orderSearchId(@Valid SampleInputs sampleInputs) {
     ModelAndView model = new ModelAndView();
     Long orderId = sampleInputs.getId().longValue();
     try {
    	 Order order = orderRepository.findById(orderId).get();
         Pipeline pipeline = order.getPipeline();
         Set <Product> products = sortByProductId(pipeline.getProducts());
         List<ProductQuantity> productsQuantityList = getProductQuantityList(products, order.getPipeline());
         printOrder = order;
       	  	model.addObject("msg", "Profil Radnog Naloga");
       	  	model.setViewName("admin/order_profile");
       	  	model.addObject("order", order);
       	  	model.addObject("productsList", products);
       	  	model.addObject("productsQuantityList", productsQuantityList);
     } catch(Exception e) {
      	  model.addObject("err", "Nije pronađen radni nalog sa ID brojem: " + sampleInputs.getId());
          // List<Order> ordersList = orderRepository.findAll(Sort.by("created").descending());
          List<Order> ordersList = orderRepository.findFirst10ByOrderCompletedOrderByIdDesc(1);
          model.addObject("message", "Lista radnih naloga");     
          model.addObject("sampleInputs", new SampleInputs());
          model.addObject("ordersList", ordersList);
          model.setViewName("home/list_orders");
     }
     return model;
    }
    
    // CREATING WORD DOCUMENT OF THE ORDER
	private void createWordDocument(Order order) throws Exception {
        WordprocessingMLPackage wordPackage = null;
		try {
			wordPackage = WordprocessingMLPackage.createPackage();
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        MainDocumentPart mainDocumentPart = wordPackage.getMainDocumentPart();
        
        File image = new File("hidrastill.png");
        byte[] fileContent = Files.readAllBytes(image.toPath());
        BinaryPartAbstractImage imagePart = BinaryPartAbstractImage.createImagePart(wordPackage, fileContent);
        Inline inline = imagePart.createImageInline("HidraStill Image", "Alt Text", 1, 2, false);
        P Imageparagraph = addImageToParagraph(inline);
        mainDocumentPart.getContent().add(Imageparagraph);
        
        ObjectFactory factory = Context.getWmlObjectFactory();   
        RPr rpr = factory.createRPr();       
        BooleanDefaultTrue b = new BooleanDefaultTrue();
        U u = factory.createU();
        u.setVal(UnderlineEnumeration.SINGLE);
        
        // DATE SECTION
        P pDate = factory.createP();
        R rDate = factory.createR();

        PPr paragraphProperties = factory.createPPr();
        Jc justification = factory.createJc();
        justification.setVal(JcEnumeration.RIGHT);
        paragraphProperties.setJc(justification);
        pDate.setPPr(paragraphProperties);
        
        LocalDate localDate = convertToLocalDateViaInstant(printOrder.getCreated());
        String weekDay = bosnianWeekDay(localDate.getDayOfWeek().name());
        int dayMonth = localDate.getDayOfMonth();
        String month = bosnianMonth(localDate.getMonth().getValue());
        int year = localDate.getYear();
        
        Text date = factory.createText();
        date.setValue("Datum: " + weekDay + dayMonth + ". " + month + " " + year);
        rDate.getContent().add(date);
        
        pDate.getContent().add(rDate);

        rpr.setB(b);
        rpr.setU(u);
        rDate.setRPr(rpr);
        
        mainDocumentPart.getContent().add(pDate);

        // ADDRESS SECTION
        R r = factory.createR();
        P pAddress = factory.createP();
        Br br = factory.createBr(); // this Br element is used break the current and go for next line

        Text t = factory.createText();
        t.setValue("BRČKO");
        r.getContent().add(t);  
        
        r.getContent().add(br);
        Text t2 = factory.createText();
        t2.setValue("Industrijska br. 4 76100 Brčko distrikt");
        r.getContent().add(t2);
        
        r.getContent().add(br);
        Text t3 = factory.createText();
        t3.setValue("Bosna i Hercegovina");
        r.getContent().add(t3);
        
        r.getContent().add(br);
        Text t4 = factory.createText();
        t4.setValue("www.hidrastil.ba");
        r.getContent().add(t4);

        pAddress.getContent().add(r);

        r.setRPr(rpr);
        
        mainDocumentPart.getContent().add(pAddress);
        
        // ORDER NUMBER SECTION
        R rOrderNum = factory.createR();
        P pOrderNum = factory.createP();
        Text tOrderNum = factory.createText();
        tOrderNum.setValue("RADNI NALOG br. " + printOrder.getId() + " / " + localDate.getYear());
        rOrderNum.getContent().add(tOrderNum);  
        pOrderNum.getContent().add(rOrderNum);
        
        PPr paragraphPropertiesON = factory.createPPr();
        Jc justificationON = factory.createJc();
        justificationON.setVal(JcEnumeration.CENTER);
        paragraphPropertiesON.setJc(justificationON);
        pOrderNum.setPPr(paragraphPropertiesON);

        RPr rprOrderNum = factory.createRPr();  
        HpsMeasure size = new HpsMeasure();
        size.setVal(BigInteger.valueOf(25));
        rprOrderNum.setSz(size);
        rprOrderNum.setB(b);
        rprOrderNum.setU(u);
        rOrderNum.setRPr(rprOrderNum);
        
        mainDocumentPart.getContent().add(pOrderNum);
        
        // WORK POSITION SECTION
        R rWorkPos = factory.createR();
        P pWorkPos = factory.createP();
        Text tWorkPos = factory.createText();
        tWorkPos.setValue("RADNA POZICIJA: " + printOrder.getWorkPosition());
        rWorkPos.getContent().add(tWorkPos);  
        pWorkPos.getContent().add(rWorkPos);

        RPr rprWorkPos = factory.createRPr();  
        rprWorkPos.setB(b);
        rprWorkPos.setU(u);
        rprWorkPos.setCaps(b);
        rWorkPos.setRPr(rprWorkPos);
        
        mainDocumentPart.getContent().add(pWorkPos);
        
        // TABLE SECTION
        int writableWidthTwips = wordPackage.getDocumentModel()
        		  .getSections().get(0).getPageDimensions().getWritableWidthTwips();
        int columnNumber = 6;
        
        // DISPLAYING TABLE HEADING
        List<P> pheadings = new ArrayList<>();
        pheadings = createHeadings();
        Tbl tblHeading = TblFactory.createTable(1, columnNumber, writableWidthTwips/columnNumber);
        List<Object> rows = tblHeading.getContent();
        for (Object row : rows) {
        	Tr tr = (Tr) row;
        	List<Object> cells = tr.getContent();
        	int count = 0;
        	for(Object cell : cells) {
        		Tc td = (Tc) cell;
        		td.getContent().add(pheadings.get(count));
        		count++;
        		}
        	}   
        mainDocumentPart.getContent().add(tblHeading);
        
        // DISPLAYING PRODUCTS IN THE TABLE
        int numOfRows = printOrder.getPipeline().getProducts().size();
        Tbl tbl = TblFactory.createTable(numOfRows, columnNumber, writableWidthTwips/columnNumber);     
        rows = tbl.getContent();
        int index = 0;
        for (Object row : rows) {
        	Tr tr = (Tr) row;
        	List<Object> cells = tr.getContent();
            List<P> pProduct = createProductValues(index);
            int count = 0;
        	for(Object cell : cells) {
        		Tc td = (Tc) cell;
        		td.getContent().add(pProduct.get(count));
        		count++;
        		}
        	index++;
        	}
        mainDocumentPart.getContent().add(tbl);
        
        // DISPLAY PACKAGING
        R rPack = factory.createR();
        P pPack = factory.createP();
        
        rPack.getContent().add(br);
        Text t5 = factory.createText();
        t5.setValue("PAKOVANJE:");
        rPack.getContent().add(t5);  

        rPack.getContent().add(br);
        Text t6 = factory.createText();
        t6.setValue(printOrder.getOrderPackaging());
        rPack.getContent().add(t6);
        
        rPack.getContent().add(br);
        Text t7 = factory.createText();
        t7.setValue("________________________________________"); 
        rPack.getContent().add(t7);
        
        pPack.getContent().add(rPack);
        rPack.setRPr(rpr);
        mainDocumentPart.getContent().add(pPack);
        
        // DISPLAY ORDER COMMENT
        P pComm = factory.createP();
        R rComm = factory.createR();

        PPr paragraphPropertiesC = factory.createPPr();
        Jc justificationC = factory.createJc();
        justificationC.setVal(JcEnumeration.RIGHT);
        paragraphPropertiesC.setJc(justificationC);
        pComm.setPPr(paragraphPropertiesC);
        
        Text t8 = factory.createText();
        t8.setValue("KOMENTAR:");
        rComm.getContent().add(t8);
        
        rComm.getContent().add(br);
        Text t9 = factory.createText();
        t9.setValue(printOrder.getOrderComment());
        rComm.getContent().add(t9);
        
        rComm.getContent().add(br);
        Text t10 = factory.createText();
        t10.setValue("________________________________________");
        rComm.getContent().add(t10);
        
        pComm.getContent().add(rComm);
        rComm.setRPr(rpr);
        mainDocumentPart.getContent().add(pComm);
        
        // DISPLAY ORDER PREPARATION
        R rPrep = factory.createR();
        P pPrep = factory.createP();
        
        Text tPrep = factory.createText();
        tPrep.setValue("PRIPREMA:");
        rPrep.getContent().add(tPrep);  
        
        rPrep.getContent().add(br);
        Text tPrep1 = factory.createText();
        tPrep1.setValue(printOrder.getOrderPrep());
        rPrep.getContent().add(tPrep1);
        
        rPrep.getContent().add(br);
        Text tPrep2 = factory.createText();
        tPrep2.setValue("________________________________________"); 
        rPrep.getContent().add(tPrep2);
        
        pPrep.getContent().add(rPrep);
        rPrep.setRPr(rpr);
        mainDocumentPart.getContent().add(pPrep);
        
        // SAVING FILE
        File exportFile = new File("document.docx");
		wordPackage.save(exportFile);
	}


	// DOWNLOAD CREATED WORD DOCUMENT OF THE ORDER
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
			LocalDate localDate = convertToLocalDateViaInstant(printOrder.getCreated());
			//Here we have mentioned it to show as attachment
			response.setHeader("Content-Disposition", String.format("attachment; filename=\"RN-" + printOrder.getId() + "-OD-" + localDate.now() + "\".docx"));

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
    
    public LocalDate convertToLocalDateViaInstant(Date dateToConvert) {
        return dateToConvert.toInstant()
          .atZone(ZoneId.systemDefault())
          .toLocalDate();
    }
    
	private String bosnianWeekDay(String weekDay) {
		switch(weekDay){
		case "MONDAY": return "ponedeljak, ";
		case "TUESDAY": return "utorak, ";
		case "WEDNESDAY": return "srijeda, ";
		case "THURSDAY": return "četvrtak, ";
		case "FRIDAY": return "petak, ";
		case "SATURDAY": return "subota, ";
		case "SUNDAY": return "nedelja, ";
		default: return "nepoznat, ";
		}
	}
	
	private String bosnianMonth(int monthYear) {
		switch(monthYear){
		case 1: return "januar";
		case 2: return "februar";
		case 3: return "mart";
		case 4: return "april";
		case 5: return "maj";
		case 6: return "juni";
		case 7: return "juli";
		case 8: return "august";
		case 9: return "septembar";
		case 10: return "oktobar";
		case 11: return "novembar";
		case 12: return "decembar";
		default: return "nepoznat, ";
		}
	}


	private List<P> createHeadings() {
		List<P> tableHeadings = new ArrayList<>();
        ObjectFactory factory = Context.getWmlObjectFactory();   
        RPr rprHeadings = factory.createRPr();  
        BooleanDefaultTrue b = new BooleanDefaultTrue();
        rprHeadings.setB(b);
        rprHeadings.setCaps(b);
        
        String [] headings = {"Redni broj", "Naziv kabine", "Dimenzija kabine", "Količina", "Napomena / Dezen / Mat", "Kupac putni pravac"};
        
        for(String heading: headings) {
            R rHeading = factory.createR();
            P pHeading = factory.createP();
            
            PPr paragraphPropertiesH = factory.createPPr();
            Jc justificationH = factory.createJc();
            justificationH.setVal(JcEnumeration.CENTER);
            paragraphPropertiesH.setJc(justificationH);
            pHeading.setPPr(paragraphPropertiesH);
            
            Text tHeading = factory.createText();
            tHeading.setValue(heading);
            rHeading.getContent().add(tHeading);  
            pHeading.getContent().add(rHeading);
            rHeading.setRPr(rprHeadings);
            tableHeadings.add(pHeading);
        }
		return tableHeadings;
	}
	

	private List<P> createProductValues(int index) {
		List<P> pProductsList = new ArrayList<>();
	    
		Set <Product> products = new TreeSet<>();
	    products = sortByProductId(printOrder.getPipeline().getProducts());
	    List<ProductQuantity> productsQuantityList = getProductQuantityList(products, printOrder.getPipeline());

		OrderWordProduct wordProduct = new OrderWordProduct();
		List<Product> productsL = new ArrayList<>(products);
		Product product = productsL.get(index);
			wordProduct.setRankNum("" + (index + 1) + ".");
			wordProduct.setProductName(product.getName());
			wordProduct.setProductDim(product.getProductSize());
			wordProduct.setQuantity("" + productsQuantityList.get(index).getQuantity());
			if(productsQuantityList.get(index).getComment() == null) {
				wordProduct.setNote("");
			} else {
				wordProduct.setNote("" + productsQuantityList.get(index).getComment());
			}
			if(productsQuantityList.get(index).getBuyer() == null) {
				wordProduct.setBuyer("");
			} else {
				wordProduct.setBuyer("" + productsQuantityList.get(index).getBuyer());
			}
		
        ObjectFactory factory = Context.getWmlObjectFactory();   
        RPr rprProduct = factory.createRPr();  
        BooleanDefaultTrue b = new BooleanDefaultTrue();
        rprProduct.setB(b);
        rprProduct.setCaps(b);
        
        String [] productValues = {wordProduct.getRankNum(), wordProduct.getProductName(), 
        						wordProduct.getProductDim(), wordProduct.getQuantity(), 
        						wordProduct.getNote(), wordProduct.getBuyer()};
        
        for(String value: productValues) {
            R rProduct = factory.createR();
            P pProduct = factory.createP();
            
            PPr paragraphPropertiesPro = factory.createPPr();
            Jc justificationPro = factory.createJc();
            justificationPro.setVal(JcEnumeration.CENTER);
            paragraphPropertiesPro.setJc(justificationPro);
            pProduct.setPPr(paragraphPropertiesPro);
            
            Text tProduct = factory.createText();
            tProduct.setValue(value);
            rProduct.getContent().add(tProduct);  
            pProduct.getContent().add(rProduct);
            rProduct.setRPr(rprProduct);
            pProductsList.add(pProduct);
        }
		return pProductsList;
	}
}