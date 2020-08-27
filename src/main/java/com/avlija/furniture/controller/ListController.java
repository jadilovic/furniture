package com.avlija.furniture.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.validation.Valid;

import com.avlija.furniture.form.SampleInputs;
import com.avlija.furniture.model.Element;
import com.avlija.furniture.model.ElementQuantity;
import com.avlija.furniture.model.ListOfProducts;
import com.avlija.furniture.model.Product;
import com.avlija.furniture.model.ProductElement;
import com.avlija.furniture.model.UnitMeasure;
import com.avlija.furniture.repository.ElementQuantityRepository;
import com.avlija.furniture.repository.ElementRepository;
import com.avlija.furniture.repository.ListOfProductsRepository;
import com.avlija.furniture.repository.ProductRepository;
import com.avlija.furniture.repository.UnitMeasureRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ListController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ListOfProductsRepository listOfProductsRepository;

    @Autowired
    private ElementRepository elementRepository;
    
    @Autowired
    private ElementQuantityRepository elementQuantityRepository;


    // CREATE LIST OF PRODUCTS
    
    @RequestMapping(value= {"admin/createlist"}, method=RequestMethod.GET)
    public ModelAndView createList() {
     ModelAndView model = new ModelAndView();
     ListOfProducts listOfProducts = new ListOfProducts();
     model.addObject("listOfProducts", listOfProducts);
     model.setViewName("admin/create_list");
     
     return model;
    }
    
    @RequestMapping(value= {"admin/createlist"}, method=RequestMethod.POST)
    public ModelAndView createList(@Valid ListOfProducts listOfProducts, BindingResult bindingResult) {
     ModelAndView model = new ModelAndView();
     ListOfProducts listExists = listOfProductsRepository.findByName(listOfProducts.getName());
     if(listExists != null) {
    		 bindingResult.rejectValue("name", "error.name", "Ovaj naziv liste proizvoda već postoji!");
     }
     if(bindingResult.hasErrors()) {
      	  model.addObject("msg", "Uneseni naziv liste proizvoda već postoji.");
          ListOfProducts newList = new ListOfProducts();
          model.addObject("listOfProducts", newList);
      	  model.setViewName("admin/create_list");
     } else {
   	  	listOfProductsRepository.save(listOfProducts);
   	  model.addObject("msg", "Naziv nove liste proizvoda je uspješno kreiran! Potrebno je dodati proizvode od kojih se sastoji lista.");
   	  model.setViewName("admin/create_list2");
      
   	  ListOfProducts newList = listOfProductsRepository.findByName(listOfProducts.getName());
      List<Product> products = new ArrayList<>();
      model.addObject("productsList", products);
      model.addObject("list", newList);
     	}
     return model;
    }
    
    
    // DISPLAY ALL LISTS
    
    @RequestMapping(value= {"home/alllists"}, method=RequestMethod.GET)
    public ModelAndView allLists() {
     ModelAndView model = new ModelAndView();
     List<ListOfProducts> listOfProductsLists = listOfProductsRepository.findAll();
     model.addObject("listOfProductsLists", listOfProductsLists);
     model.setViewName("home/list_of_products_lists");
     
     return model;
    }
    
    // LIST PROFILE
    
    @RequestMapping(value= {"home/listprofile/{id}"}, method=RequestMethod.GET)
    public ModelAndView listProfile(@PathVariable(name = "id") Integer id) {
     ModelAndView model = new ModelAndView();
     ListOfProducts listOfProducts = listOfProductsRepository.findById(id).get();
     model.addObject("list", listOfProducts);
     model.addObject("productsList", listOfProducts.getProducts());
     model.setViewName("home/list_profile");
     
     return model;
    }
    
    
    // ADD PRODUCTS TO THE LIST
    
    @RequestMapping(value= {"admin/addproducts/{id}"}, method=RequestMethod.GET)
    public ModelAndView addElement(@PathVariable(name = "id") Integer id) {
     ModelAndView model = new ModelAndView();
     ListOfProducts listOfProducts = listOfProductsRepository.findById(id).get();
     SampleInputs sampleInputs = new SampleInputs();
     sampleInputs.setListId(id);
     model.addObject("list", listOfProducts);
     model.addObject("sampleInputs", sampleInputs);
     model.addObject("productsList", listOfProducts.getProducts());
     model.setViewName("admin/add_products");
     
     return model;
    }
    
    /**
    @RequestMapping(value= {"admin/addproduct"}, method=RequestMethod.POST)
    public ModelAndView addElementToProduct(@Valid Product product) {
     ModelAndView model = new ModelAndView();
     List <Element> elements = new ArrayList<Element>();
     elements = product.getElements();
     	Product createdProduct = productRepository.findById(product.getId()).get();
     	for(Element element: elements) {
     		ProductElement productElement = new ProductElement(createdProduct.getId(), element.getId());
     		if(elementQuantityExists(productElement)) {
     			break;
     		}
     		ElementQuantity elementQuantity = new ElementQuantity(productElement, 0);
     		elementQuantityRepository.save(elementQuantity);
     	}
     	createdProduct.setElements(elements);
   	  	productRepository.save(createdProduct);
   	  	List<ElementQuantity> elementsQuantityList = getElementQuantityList(elements, createdProduct);
   	  model.addObject("msg", "Izvršena dopuna - izmjena elemenata");
   	  model.setViewName("admin/create_product2");
     model.addObject("product", createdProduct);
     model.addObject("elementsList", elements);
     model.addObject("elementsQuantityList", elementsQuantityList);
     return model;
    }
**/

    // SEARCH PRODUCT BY ID TO BE ADDED TO THE LIST
    
    @RequestMapping(value= {"home/searchproductid"}, method=RequestMethod.POST)
    public ModelAndView searchProductById(@Valid SampleInputs sampleInputs) {
     ModelAndView model = new ModelAndView();
     List <Product> newProducts = new ArrayList<Product>();
    	ListOfProducts listOfProducts = listOfProductsRepository.findById(sampleInputs.getListId()).get();
      	Product product;
    	try {
    		product = productRepository.findById(sampleInputs.getPrdId()).get();
    	} catch(Exception ex) {
    		product = null;
    	}
      	List<Product> products = listOfProducts.getProducts();
    	 if(product == null) {
        	 model.addObject("err", "Nije pronađen proizovd sa ID brojem: " + sampleInputs.getPrdId());
 	 		 model.addObject("productsList", products);        	 
    	 	}
    	 if(productAlreadyInList(product, products)){
        	 model.addObject("err", "Pronađen proizvod sa ID brojem: '" + sampleInputs.getPrdId() + "', ali već postoji u listi.");
 	 		 model.addObject("productsList", products);    	 	
    	 	} else {
    	 		newProducts.add(product);
    	 		for(Product item: products) {
    	 			newProducts.add(item);
    	 		}
    	 		model.addObject("productsList", newProducts);
    	 		model.addObject("msg", "Pronađen proizvod sa ID brojem: " + sampleInputs.getPrdId());		
    	 		}
          model.addObject("list", listOfProducts);
          model.addObject("sampleInputs", sampleInputs);
          model.setViewName("admin/add_products");
     return model;
    }

	private boolean productAlreadyInList(Product product, List<Product> products) {
		for(Product check: products) {
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
   	ListOfProducts listOfProducts = listOfProductsRepository.findById(sampleInputs.getListId()).get();
         List<Product> foundProducts = productRepository.findByNameContaining(keyWord);
         List<Product> products = listOfProducts.getProducts();
         if(foundProducts.isEmpty()) {
         	  model.addObject("err", "Nije pronađen proizvod koji sadrži ključnu riječ: " + keyWord);
              model.addObject("productsList", products);
         	} else {
         		List<Product> selectionProducts = new ArrayList<>();
         		foundProducts.removeAll(products);
         		if(foundProducts.isEmpty()) {
             		model.addObject("msg", "Lista elemenata koji sadrže ključnu riječ: '" + keyWord + "' se već nalaze u proizvodu.");
         			} else {
             		model.addObject("msg", "Lista elemenata koji sadrže ključnu riječ: " + keyWord);
             			for(Product item: foundProducts) {
             				selectionProducts.add(item);
             				}
         				}
    	 		for(Product item: products) {
    	 			selectionProducts.add(item);
    	 			}
      			model.addObject("productsList", selectionProducts);         		
         		}
        model.addObject("list", listOfProducts);
        model.addObject("sampleInputs", sampleInputs);         
  			model.setViewName("admin/add_products");
         	return model;
    	}
    
	
	
    /**
	@RequestMapping(value="admin/quantity/{productId}/{elementId}", method=RequestMethod.GET)
    public ModelAndView getSpecificQuestions(@PathVariable String productId, @PathVariable String elementId) {
    	ModelAndView model = new ModelAndView();
    	int prdId = Integer.parseInt(productId);
    	int elmId = Integer.parseInt(elementId);
    	ProductElement productElement = new ProductElement(prdId, elmId);
    	ElementQuantity elementQuantity = elementQuantityRepository.findById(productElement).get();
    	Product product = productRepository.findById(prdId).get();
    	Element element = elementRepository.findById(elmId).get();
    	SampleInputs sampleInputs = new SampleInputs();
    	sampleInputs.setElmId(elmId);
    	sampleInputs.setPrdId(prdId);
    	sampleInputs.setQuantity(elementQuantity.getQuantity());
    	model.setViewName("admin/element_add_quantity");
        model.addObject("sampleInputs", sampleInputs);
        model.addObject("element", element);
        model.addObject("elementQuantity", elementQuantity);        
        model.addObject("msg", "Dodaj potrebnu količinu elementa u proizvodu: " + product.getName());
        return model;
    }
    
    @RequestMapping(value= {"admin/elementquantity"}, method=RequestMethod.POST)
    public ModelAndView addedElementQuantity(@Valid SampleInputs sampleInputs) {
     ModelAndView model = new ModelAndView();
     List <Element> elements = new ArrayList<Element>();
     	Product createdProduct = productRepository.findById(sampleInputs.getPrdId()).get();
        elements = createdProduct.getElements();
        Element element = elementRepository.findById(sampleInputs.getElmId()).get();
        ProductElement productElement = new ProductElement(sampleInputs.getPrdId(), sampleInputs.getElmId());
        ElementQuantity elementQuantity = elementQuantityRepository.findById(productElement).get();
        elementQuantity.setQuantity(sampleInputs.getQuantity());
        elementQuantityRepository.save(elementQuantity);
   	  	
        List<ElementQuantity> elementsQuantityList = getElementQuantityList(elements, createdProduct);
   	  model.addObject("msg", "Izvršena dopuna - izmjena količine elemenata: " + element.getName());
   	  model.setViewName("admin/create_product2");
     model.addObject("product", createdProduct);
     model.addObject("elementsList", elements);
     model.addObject("elementsQuantityList", elementsQuantityList);
     return model;
    }
    
    private List<ElementQuantity> getElementQuantityList(List<Element> elementList, Product product) {
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

    
    private boolean elementQuantityExists(ProductElement productElement) {
    	try {
    		ElementQuantity elementQuantity = elementQuantityRepository.findById(productElement).get();
    		if(elementQuantity == null) {
    			return false;
    		} else {
    			return true;
    		}
    	} catch (Exception e) {
    		return false;
    	}
	}
    **/
}