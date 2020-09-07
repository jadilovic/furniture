package com.avlija.furniture.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.validation.Valid;

import com.avlija.furniture.form.SampleInputs;
import com.avlija.furniture.model.Pipeline;
import com.avlija.furniture.model.PipelineProduct;
import com.avlija.furniture.model.Product;
import com.avlija.furniture.model.ProductQuantity;
import com.avlija.furniture.repository.PipelineRepository;
import com.avlija.furniture.repository.ProductQuantityRepository;
import com.avlija.furniture.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class PipelineController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PipelineRepository pipelineRepository;

    @Autowired
    private ProductQuantityRepository productQuantityRepository;
    

    // CREATE PIPELINE OF PRODUCTS GET
    
    @RequestMapping(value= {"admin/createpipeline"}, method=RequestMethod.GET)
    public ModelAndView createPipeline() {
     ModelAndView model = new ModelAndView();
     Pipeline pipeline = new Pipeline();
     model.addObject("pipeline", pipeline);
     model.setViewName("admin/create_pipeline");
     
     return model;
    }
    
    
    // CREATE PIPELINE POST
    
    @RequestMapping(value= {"admin/createpipeline"}, method=RequestMethod.POST)
    public ModelAndView createPipeline(@Valid Pipeline pipeline, BindingResult bindingResult) {
     ModelAndView model = new ModelAndView();
     Pipeline pipelineExists = pipelineRepository.findByName(pipeline.getName());
     if(pipelineExists != null) {
    		 bindingResult.rejectValue("name", "error.name", "Ovaj naziv liste proizvoda već postoji!");
     }
     if(bindingResult.hasErrors()) {
      	  model.addObject("msg", "Uneseni naziv liste proizvoda već postoji.");
          Pipeline newPipeline = new Pipeline();
          model.addObject("pipeline", newPipeline);
      	  model.setViewName("admin/create_pipeline");
     } else {
   	  	pipelineRepository.save(pipeline);
      
   	  Pipeline createdPipeline = pipelineRepository.findByName(pipeline.getName());
      List<Product> products = new ArrayList<>();
      SampleInputs sampleInputs = new SampleInputs();
   	  model.addObject("msg", "Naziv nove liste proizvoda je uspješno kreiran! Potrebno je dodati proizvode od kojih se sastoji lista.");
      model.addObject("sampleInputs", sampleInputs);
      model.addObject("productsList", products);
      model.addObject("pipeline", createdPipeline);
   	  model.setViewName("admin/create_pipeline2");
     	}
     return model;
    }
    
    
    // DISPLAY ALL PIPELINES
    
    @RequestMapping(value= {"home/allpipelines"}, method=RequestMethod.GET)
    public ModelAndView allPipelines() {
     ModelAndView model = new ModelAndView();
     List<Pipeline> pipelines = pipelineRepository.findAll();
     model.addObject("pipelines", pipelines);
     model.setViewName("home/list_of_pipelines");
     
     return model;
    }
    
    
    // PIPELINE PROFILE
    
    @RequestMapping(value= {"home/pipelineprofile/{id}"}, method=RequestMethod.GET)
    public ModelAndView listProfile(@PathVariable(name = "id") Integer id) {
     ModelAndView model = new ModelAndView();
     Pipeline pipeline = pipelineRepository.findById(id).get();
     model.addObject("pipeline", pipeline);
     model.addObject("productsList", pipeline.getProducts());
     model.setViewName("home/pipeline_profile");
     
     return model;
    }
    
    
    // ADD PRODUCTS TO THE PIPELINE
    
    @RequestMapping(value= {"admin/add/{id}"}, method=RequestMethod.GET)
    public ModelAndView addProductsToPipelineGET(@PathVariable(name = "id") Integer id) {
     ModelAndView model = new ModelAndView();
     Pipeline pipeline = pipelineRepository.findById(id).get();
     
     SampleInputs sampleInputs = new SampleInputs();
  	System.out.println("PRODUCTS IN OBJECT:" + pipeline.getProducts());
     sampleInputs.setPipelineId(id);
     model.addObject("sampleInputs", sampleInputs);
     model.addObject("pipeline", pipeline);
     model.addObject("productsList", pipeline.getProducts());
     model.addObject("emptyPipeline", pipeline.getProducts().isEmpty());
     model.setViewName("admin/add_products");
     
     return model;
    }
    
    
    // ADDING PRODUCTS TO THE PIPELINE
    
    @RequestMapping(value= {"admin/addproducts"}, method=RequestMethod.POST)
    public ModelAndView addProductsToPipelinePOST(@Valid Pipeline pipeline) {
     ModelAndView model = new ModelAndView();
     Set <Product> selectedProducts = new HashSet<>();
     selectedProducts = pipeline.getProducts();

     Pipeline savedPipeline = pipelineRepository.findById(pipeline.getId()).get();

     	for(Product product: selectedProducts) {
     		PipelineProduct pipelineProduct = new PipelineProduct(pipeline.getId(), product.getId());
     		if(productQuantityExists(pipelineProduct)) {
     			break;
     		}
     		ProductQuantity productQuantity = new ProductQuantity(pipelineProduct, 0, null, null);
     		productQuantityRepository.save(productQuantity);
     	}
     	
     	savedPipeline.setProducts(selectedProducts);
   	  	pipelineRepository.save(savedPipeline);
   	  	
   	  	List<ProductQuantity> productsQuantityList = getProductQuantityList(selectedProducts, savedPipeline);

   	  model.addObject("msg", "Izvršena dopuna - izmjena proizvoda");
   	  model.setViewName("admin/create_pipeline2");
     model.addObject("pipeline", savedPipeline);
     model.addObject("productsList", savedPipeline.getProducts());
     model.addObject("productsQuantityList", productsQuantityList);
     return model;
    }

	
	   // SEARCH PRODUCT BY ID TO BE ADDED TO THE PIPELINE

	@RequestMapping(value= {"home/searchproductid"}, method=RequestMethod.POST)
    public ModelAndView searchProductById(@Valid SampleInputs sampleInputs) {
     ModelAndView model = new ModelAndView();
     	
     	Set <Product> pipelineProducts = new HashSet<Product>();
    	Pipeline pipeline = pipelineRepository.findById(sampleInputs.getPipelineId()).get();
      	pipelineProducts = pipeline.getProducts();
      	Boolean emptyPipeline = pipelineProducts.isEmpty();
      	
     	List <Product> products = new ArrayList<Product>();
      	
      	Product product;
    	try {
    		product = productRepository.findById(sampleInputs.getPrdId()).get();
    	} catch(Exception ex) {
    		product = null;
    	}
    	
    	 if(product == null) {
        	 model.addObject("err", "Nije pronađen proizovd sa ID brojem: " + sampleInputs.getPrdId());
 	 		 model.addObject("productsList", pipelineProducts); 
    	 	}
    	 else if(productAlreadyInList(product, pipelineProducts)){
        	 model.addObject("err", "Pronađen proizvod sa ID brojem: '" + sampleInputs.getPrdId() + "', ali već postoji u listi.");
 	 		 model.addObject("productsList", pipelineProducts);    	 	
    	 	} else {
    	 		products.add(product);
    	 		for(Product item: pipelineProducts) {
    	 			products.add(item);
    	 		}
    	 		model.addObject("productsList", products);
    	 		model.addObject("msg", "Pronađen proizvod sa ID brojem: " + sampleInputs.getPrdId());		
    	 	}

    	 model.addObject("emptyPipeline", emptyPipeline);
         model.addObject("pipeline", pipeline);
         model.addObject("sampleInputs", sampleInputs);
         model.setViewName("admin/add_products");
     return model;
    }

	
	// CHECKING IF THE PRODUCT IS IN THE PIPELINE
	
	private boolean productAlreadyInList(Product product, Set<Product> pipelineProducts) {
		for(Product check: pipelineProducts) {
			if(check.getId() == product.getId()) {
				return true;
			}
		}
		return false;
	}
    
	
	// SEARCH PRODUCT BY KEYWORD
	
	@RequestMapping(value= {"home/productsearchkeyword2"}, method=RequestMethod.POST)
    public ModelAndView productSearchKeyWord(@Valid SampleInputs sampleInputs) {
     ModelAndView model = new ModelAndView();
     String keyWord = sampleInputs.getKeyWord();
   		Pipeline pipeline = pipelineRepository.findById(sampleInputs.getPipelineId()).get();
         List<Product> foundProducts = productRepository.findByNameContaining(keyWord);
         Set <Product> pipelineProducts = pipeline.getProducts(); 		 
         //model.addObject("emptyListOfProducts", products.isEmpty());

         if(foundProducts.isEmpty()) {
         	  model.addObject("err", "Nije pronađen proizvod koji sadrži ključnu riječ: " + keyWord);
              model.addObject("productsList", pipelineProducts);
         	} 
         else {
         		List<Product> selectionProducts = new ArrayList<>();
         		foundProducts.removeAll(pipelineProducts);
         		if(foundProducts.isEmpty()) {
             		model.addObject("msg", "Lista elemenata koji sadrže ključnu riječ: '" + keyWord + "' se već nalaze u proizvodu.");
                    model.addObject("productsList", pipelineProducts);
         		} else {
             		model.addObject("msg", "Lista elemenata koji sadrže ključnu riječ: " + keyWord);
             			
             			for(Product item: foundProducts) {
             				selectionProducts.add(item);
             				}

             			for(Product item: pipelineProducts) {
             				selectionProducts.add(item);
             				}
             		model.addObject("productsList", selectionProducts); 
 				}
         	}
        model.addObject("pipeline", pipeline);
        model.addObject("sampleInputs", sampleInputs);         
  			model.setViewName("admin/add_products");
         	return model;
    	}
    
	
	// ADD PRODUCT QUANTITY IN THE PIPELINE
	
	@RequestMapping(value="admin/productquantity/{pipelineId}/{productId}", method=RequestMethod.GET)
    public ModelAndView addProductQuantityInThePipeline(@PathVariable String pipelineId, @PathVariable String productId) {
    	ModelAndView model = new ModelAndView();
    	int pplId = Integer.parseInt(pipelineId);
    	int prdId = Integer.parseInt(productId);
    	PipelineProduct pipelineProduct = new PipelineProduct(pplId, prdId);
    	ProductQuantity productQuantity = productQuantityRepository.findById(pipelineProduct).get();
    	Pipeline pipeline = pipelineRepository.findById(pplId).get();    	
    	Product product = productRepository.findById(prdId).get();
    	SampleInputs sampleInputs = new SampleInputs();
    	sampleInputs.setPipelineId(pplId);
    	sampleInputs.setPrdId(prdId);
    	sampleInputs.setQuantity(productQuantity.getQuantity());
    	sampleInputs.setPrdComment(productQuantity.getComment());
    	sampleInputs.setPrdBuyer(productQuantity.getBuyer());
    	model.setViewName("admin/product_add_quantity");
        model.addObject("sampleInputs", sampleInputs);
        model.addObject("product", product);
        model.addObject("productQuantity", productQuantity);        
        model.addObject("msg", "Dodaj potrebnu količinu proizvoda u listu: " + pipeline.getName());
        return model;
    }
    

	// ADD PRODUCT QUANTITY IN THE PIPELINE POST

    @RequestMapping(value= {"admin/productquantity"}, method=RequestMethod.POST)
    public ModelAndView addProductQuantity(@Valid SampleInputs sampleInputs) {
     ModelAndView model = new ModelAndView();
     Set <Product> products = new TreeSet<>();
     	Pipeline createdPipeline = pipelineRepository.findById(sampleInputs.getPipelineId()).get();
        products = createdPipeline.getProducts();
        Product product = productRepository.findById(sampleInputs.getPrdId()).get();
        PipelineProduct pipelineProduct = new PipelineProduct(sampleInputs.getPipelineId(), sampleInputs.getPrdId());
        ProductQuantity productQuantity = productQuantityRepository.findById(pipelineProduct).get();
        productQuantity.setQuantity(sampleInputs.getQuantity());
        productQuantity.setComment(sampleInputs.getPrdComment());
        productQuantity.setBuyer(sampleInputs.getPrdBuyer());
        productQuantityRepository.save(productQuantity);
   	  	
        List<ProductQuantity> productQuantityList = getProductQuantityList(products, createdPipeline);
   	  model.addObject("msg", "Izvršena dopuna - izmjena količine proizvoda: " + product.getName());
   	  model.setViewName("admin/create_pipeline2");
     model.addObject("pipeline", createdPipeline);
     model.addObject("productsList", products);
     model.addObject("productsQuantityList", productQuantityList);
     return model;
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
    
    
    // FIND OUT IF THE PRODUCT QUANTITY EXISTS

	private boolean productQuantityExists(PipelineProduct pipelineProduct) {
    	try {
    		ProductQuantity productQuantity = productQuantityRepository.findById(pipelineProduct).get();
    		if(productQuantity == null) {
    			return false;
    		} else {
    			return true;
    		}
    	} catch (Exception e) {
    		return false;
    	}
	}
	
}