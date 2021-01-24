package com.avlija.furniture.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.avlija.furniture.form.SampleInputs;
import com.avlija.furniture.model.Element;
import com.avlija.furniture.model.ElementQuantity;
import com.avlija.furniture.model.Pipeline;
import com.avlija.furniture.model.PipelineProduct;
import com.avlija.furniture.model.Product;
import com.avlija.furniture.model.ProductQuantity;
import com.avlija.furniture.repository.PipelineRepository;
import com.avlija.furniture.repository.ProductQuantityRepository;
import com.avlija.furniture.repository.ProductRepository;
import com.avlija.furniture.service.ProductIdComp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
    
    private static List<ProductQuantity> productsQuantityList;
    
    private static Set<Product> productsList;
    
    private static Pipeline savedPipeline;
    
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
       // Active Pipeline means that it has not been added to the Order - products are still being added
    	 pipeline.setActive(1);
   	  	pipelineRepository.save(pipeline);
      
   	  Pipeline createdPipeline = pipelineRepository.findByName(pipeline.getName());
      Set<Product> products = new TreeSet<>();
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
    public String allPipelines(HttpServletRequest request, Model model) {
    	
        int page = 0; //default page number is 0
        int size = 10; //default page size is 10
        
        if (request.getParameter("page") != null && !request.getParameter("page").isEmpty()) {
            page = Integer.parseInt(request.getParameter("page")) - 1;
        }

        if (request.getParameter("size") != null && !request.getParameter("size").isEmpty()) {
            size = Integer.parseInt(request.getParameter("size"));
        }
    	
     Page <Pipeline> pipelines = null;
     pipelines = pipelineRepository.findAll(PageRequest.of(page, size, Sort.by("id").descending()));
     
     model.addAttribute("sampleInputs", new SampleInputs());
     model.addAttribute("pipelines", pipelines);
     
     return "home/list_all_pipelines";
    }
    
    
    // PIPELINE PROFILE
    
    @RequestMapping(value= {"home/pipelineprofile/{id}"}, method=RequestMethod.GET)
    public ModelAndView listProfile(@PathVariable(name = "id") Integer id) {
     ModelAndView model = new ModelAndView();
     Pipeline pipeline = pipelineRepository.findById(id).get();
	 Set<Product> productsList = sortByProductId(pipeline.getProducts());
	 List<ProductQuantity> productsQuantityList = getProductQuantityList(productsList, pipeline);
	 model.addObject("productsQuantityList", productsQuantityList);
     model.addObject("pipeline", pipeline);
     model.addObject("productsList", productsList);
     model.setViewName("home/pipeline_profile");
     
     return model;
    }
    
    // ADD PRODUCTS TO THE PIPELINE
    
    @RequestMapping(value= {"admin/add/{id}"}, method=RequestMethod.GET)
    public ModelAndView addProductsToPipelineGET(@PathVariable(name = "id") Integer id) {
     ModelAndView model = new ModelAndView();
     savedPipeline = pipelineRepository.findById(id).get();
     Set<Product> products = sortByProductId(savedPipeline.getProducts());
     
     SampleInputs sampleInputs = new SampleInputs();
  	System.out.println("PRODUCTS IN OBJECT:" + savedPipeline.getProducts());
     sampleInputs.setPipelineId(id);
     model.addObject("sampleInputs", sampleInputs);
     model.addObject("pipeline", savedPipeline);
     model.addObject("productsList", products);
     model.addObject("emptyProducts", savedPipeline.getProducts().isEmpty());
     model.setViewName("admin/add_products");
     
     return model;
    }
    
    
    // CHANGE PIPELINE
	@RequestMapping(value= {"admin/changepipeline/{id}"}, method=RequestMethod.GET)
    public ModelAndView changePipeline(@PathVariable(name = "id") Integer id) {
     ModelAndView model = new ModelAndView();
     Pipeline pipeline = pipelineRepository.findById(id).get();
	 Set<Product> productsList = sortByProductId(pipeline.getProducts());
	 List<ProductQuantity> productsQuantityList = getProductQuantityList(productsList, pipeline);
	  model.addObject("msg", "Izmjene specifikacija liste proizvoda");
	  model.setViewName("admin/create_pipeline2");
	  model.addObject("pipeline", pipeline);
	  model.addObject("productsList", productsList);
	  model.addObject("productsQuantityList", productsQuantityList);
	  return model;
    }
    
    // ADDING PRODUCTS TO THE PIPELINE
    
    @RequestMapping(value= {"admin/addproducts"}, method=RequestMethod.POST)
    public ModelAndView addProductsToPipelinePOST(@Valid Pipeline pipeline) {
     ModelAndView model = new ModelAndView();
     Set <Product> selectedProducts = sortByProductId(pipeline.getProducts());

     savedPipeline = pipelineRepository.findById(pipeline.getId()).get();

     	for(Product product: selectedProducts) {
     		PipelineProduct pipelineProduct = new PipelineProduct(pipeline.getId(), product.getId());
     		if(productQuantityExists(pipelineProduct)) {
     			continue;
     		}
     		System.out.println("TEST TEST TEST HERE I AM");
     		ProductQuantity productQuantity = new ProductQuantity(pipelineProduct, 0, null, null);
     		productQuantityRepository.save(productQuantity);
     	}
     	
     	savedPipeline.setProducts(selectedProducts);
   	  	pipelineRepository.save(savedPipeline);
   	  	
   	  	productsQuantityList = getProductQuantityList(selectedProducts, savedPipeline);
   	  	productsList = sortByProductId(savedPipeline.getProducts());
   	  model.addObject("msg", "Izvršena dopuna - izmjena liste");
   	  model.setViewName("admin/create_pipeline2");
     model.addObject("pipeline", savedPipeline);
     model.addObject("productsList", productsList);
     model.addObject("productsQuantityList", productsQuantityList);
     return model;
    }
    
    // If browser back button is clicked display current list of products in the pipeline
    @RequestMapping(value= {"/admin/addproducts"}, method=RequestMethod.GET)
    public ModelAndView redirectBackToPipelineList() {
   	  ModelAndView model = new ModelAndView();
   	  model.addObject("msg", "Povratak na trenutnu listu proizvoda");
   	  model.setViewName("admin/create_pipeline2");
     model.addObject("pipeline", savedPipeline);
     model.addObject("productsList", productsList);
     model.addObject("productsQuantityList", productsQuantityList);
   	  return model;
    }
    
	   // SEARCH PRODUCT BY ID TO BE ADDED TO THE PIPELINE
	@RequestMapping(value= {"home/searchproductid"}, method=RequestMethod.POST)
    public ModelAndView searchProductById(@Valid SampleInputs sampleInputs) {
     ModelAndView model = new ModelAndView();
     	
    	Pipeline pipeline = pipelineRepository.findById(sampleInputs.getPipelineId()).get();
    	Set <Product> pipelineProducts = sortByProductId(pipeline.getProducts());   
    	
    	Boolean emptyProducts;
     	List <Product> products = new ArrayList<>();
      	
      	Product product;
    	try {
    		product = productRepository.findById(sampleInputs.getPrdId()).get();
    	} catch(Exception ex) {
    		product = null;
    	}
    	
    	 if(product == null) {
        	 model.addObject("err", "Nije pronađen proizovd sa ID brojem: " + sampleInputs.getPrdId());
 	 		 model.addObject("productsList", pipelineProducts); 
 	 		 emptyProducts = pipelineProducts.isEmpty();
    	 	}
    	 else if(productAlreadyInList(product, pipelineProducts)){
        	 model.addObject("err", "Pronađen proizvod sa ID brojem: '" + sampleInputs.getPrdId() + "', ali već postoji u listi.");
 	 		 model.addObject("productsList", pipelineProducts);    	 
 	 		 emptyProducts = pipelineProducts.isEmpty();
    	 } else {
    	 	products.add(product);
    	 	for(Product item: pipelineProducts) {
    	 		products.add(item);
    	 		}
    	 	model.addObject("productsList", products);
    	 	model.addObject("msg", "Pronađen proizvod sa ID brojem: " + product.getId());
    	 	emptyProducts = products.isEmpty();
    	 }

    	 model.addObject("emptyProducts", emptyProducts);
         model.addObject("pipeline", pipeline);
         model.addObject("sampleInputs", sampleInputs);
         model.setViewName("admin/add_products");
     return model;
    }
	
    // IF BROWSER BACK BUTTON IS PRESSED REDIRECT TO ADDING NEW PRODUCTS TO THE PIPELINE
	 @RequestMapping(value= {"home/searchproductid"}, method=RequestMethod.GET)
	 public String redirectBackToAddProducts() {
		 return "redirect:/admin/add/" + savedPipeline.getId();
	 }

	// SEARCH PRODUCT BY KEYWORD TO BE ADDED TO THE PIPELINE
	@RequestMapping(value= {"home/productsearchkeyword2"}, method=RequestMethod.POST)
    public ModelAndView productSearchKeyWord(@Valid SampleInputs sampleInputs) {
     ModelAndView model = new ModelAndView();
     String keyWord = sampleInputs.getKeyWord();
   		Pipeline pipeline = pipelineRepository.findById(sampleInputs.getPipelineId()).get();
         List <Product> foundProducts = productRepository.findByNameContaining(keyWord);
         Set <Product> pipelineProducts = sortByProductId(pipeline.getProducts()); 		 
         //model.addObject("emptyListOfProducts", products.isEmpty());

         if(foundProducts.isEmpty()) {
         	  model.addObject("err", "Nije pronađen proizvod koji sadrži ključnu riječ: " + keyWord);
              model.addObject("productsList", pipelineProducts);
         	} 
         else {
         		List<Product> selectionProducts = new ArrayList<>();
         		List<Product> checkList = new ArrayList<>();
         		for(Product product: pipelineProducts) {
         			checkList.add(product);
         		}
         		foundProducts.removeAll(checkList);
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
    
    // IF BROWSER BACK BUTTON IS PRESSED REDIRECT TO ADDING NEW PRODUCTS TO THE PIPELINE
	 @RequestMapping(value= {"home/productsearchkeyword2"}, method=RequestMethod.GET)
	 public String redirectToAddProductsToPipeline() {
		 return "redirect:/admin/add/" + savedPipeline.getId();
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
     	Pipeline createdPipeline = pipelineRepository.findById(sampleInputs.getPipelineId()).get();
        Set<Product> products = sortByProductId(createdPipeline.getProducts());
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
    
    //SEARCH PIPELINE BY ID
    
    @RequestMapping(value= {"home/pipelinesearchid"}, method=RequestMethod.POST)
    public ModelAndView pipelineSearchId(@Valid SampleInputs sampleInputs) {
     ModelAndView model = new ModelAndView();
     int pipelineId = sampleInputs.getPipelineId();
     try {
         Pipeline pipeline = pipelineRepository.findById(pipelineId).get();
         Set<Product> productsList = sortByProductId(pipeline.getProducts());
    	 List<ProductQuantity> productsQuantityList = getProductQuantityList(productsList, pipeline);

    	 model.addObject("productsQuantityList", productsQuantityList);
         model.addObject("pipeline", pipeline);
         model.addObject("productsList", productsList);
         model.setViewName("home/pipeline_profile");
     } catch(Exception e) {
      	  model.addObject("err", "Nije pronađena lista sa ID brojem: " + pipelineId);
          //List<Pipeline> pipelines = pipelineRepository.findAll(Sort.by("id").descending());
          List<Pipeline> pipelines = pipelineRepository.findFirst10ByActive(1, Sort.by("id").descending());
          model.addObject("sampleInputs", sampleInputs);
          model.addObject("pipelines", pipelines);      	  
       	  model.setViewName("home/list_of_pipelines");
     }
     return model;
    }
    
    
    // SEARCH PIPELINES BY KEYWORD
    
    @RequestMapping(value= {"home/pipelinesearchkeyword"}, method=RequestMethod.POST)
    public ModelAndView pipelineSearchKeyWord(@Valid SampleInputs sampleInputs) {
     ModelAndView model = new ModelAndView();
     String keyWord = sampleInputs.getKeyWord();
         List<Pipeline> pipelinesList = pipelineRepository.findByNameContaining(keyWord);
         if(pipelinesList.isEmpty()) {
         	  model.addObject("err", "Nije pronađena lista koji sadrži ključnu riječ: " + keyWord);
              // List<Pipeline> pipelines = pipelineRepository.findAll(Sort.by("id").descending());
              List<Pipeline> pipelines = pipelineRepository.findFirst10ByActive(1, Sort.by("id").descending());
              model.addObject("pipelines", pipelines);      	  
         	} else {
         		model.addObject("msg", "Lista proizvoda koja sadrže ključnu riječ: " + keyWord);
                model.addObject("pipelines", pipelinesList);      	  
         		}
         model.addObject("sampleInputs", sampleInputs);
      	 model.setViewName("home/list_of_pipelines");
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
    
    // SORTING PRODUCTS IN THE PIPELINE BY PRODUCT ID
    private Set<Product> sortByProductId(Set<Product> products) {
		Set<Product> sortedProducts = new TreeSet<>(new ProductIdComp());
	     for(Product product: products) {
	    	 sortedProducts.add(product);
	     	}
		return sortedProducts;
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