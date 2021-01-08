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
import com.avlija.furniture.model.Product;
import com.avlija.furniture.model.ProductElement;
import com.avlija.furniture.repository.ElementQuantityRepository;
import com.avlija.furniture.repository.ElementRepository;
import com.avlija.furniture.repository.OrderRepository;
import com.avlija.furniture.repository.ProductRepository;
import com.avlija.furniture.service.ElementNameSorter;
import com.avlija.furniture.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class SearchController {

    @Autowired
    private UserService userService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ElementRepository elementRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private ElementQuantityRepository elementQuantityRepository;
    
    private static List<Element> elementsByKeyWord;
    private static String keyWord;
    
    // SEARCH PRODUCTS
    @RequestMapping(value= {"home/productsearch"}, method=RequestMethod.GET)
    public ModelAndView productSearch() {
     ModelAndView model = new ModelAndView();
     SampleInputs sampleInputs = new SampleInputs();
     model.addObject("sampleInputs", sampleInputs);
     model.setViewName("home/search_product");
     
     return model;
    }
    
    @RequestMapping(value= {"home/productsearchid"}, method=RequestMethod.POST)
    public ModelAndView productSearchId(@Valid SampleInputs sampleInputs) {
     ModelAndView model = new ModelAndView();
     int productId = sampleInputs.getId();
     try {
         Product product = productRepository.findById(productId).get();
         Set <Element> elements = sortElementsByName(product.getElements());
       	  	Set<ElementQuantity> elementsQuantityList = getElementQuantityList(elements, product);
       	  	model.addObject("msg", "Informacije o proizvodu");
       	  	model.setViewName("home/product_profile");
         model.addObject("product", product);
         model.addObject("elementsList", elements);
         model.addObject("elementsQuantityList", elementsQuantityList);
     } catch(Exception e) {
      	  model.addObject("err", "Nije pronađen proizvod sa ID brojem: " + productId);
       	  model.setViewName("home/search_product");
     }
     return model;
    }
    
    // If browser back button is clicked display return to product search beginning
    @RequestMapping(value= {"/home/productsearchid"}, method=RequestMethod.GET)
    public ModelAndView redirectIdBackToProductSearch() {
        ModelAndView model = new ModelAndView();
        SampleInputs sampleInputs = new SampleInputs();
        model.addObject("sampleInputs", sampleInputs);
        model.setViewName("home/search_product");
   	  return model;
    }
    
    @RequestMapping(value= {"home/productsearchkeyword"}, method=RequestMethod.POST)
    public ModelAndView productSearchKeyWord(@Valid SampleInputs sampleInputs) {
     ModelAndView model = new ModelAndView();
     String keyWord = sampleInputs.getKeyWord();
         List<Product> productsList = productRepository.findByNameContaining(keyWord);
         if(productsList.isEmpty()) {
         	  model.addObject("err", "Nije pronađen proizvod koji sadrži ključnu riječ: " + keyWord);
           	  model.setViewName("home/search_product");
         	} else {
         		model.addObject("msg", "Lista proizvoda koji sadrže ključnu riječ: " + keyWord);
         		model.setViewName("home/list_products");
         		model.addObject("productsList", productsList);
         		}
         	return model;
    	}
    
    // If browser back button is clicked display return to product search beginning
    @RequestMapping(value= {"/home/productsearchkeyword"}, method=RequestMethod.GET)
    public ModelAndView redirectKeywordBackToProductSearch() {
        ModelAndView model = new ModelAndView();
        SampleInputs sampleInputs = new SampleInputs();
        model.addObject("sampleInputs", sampleInputs);
        model.setViewName("home/search_product");
   	  return model;
    }
    
    @RequestMapping(value= {"home/searchproductname"}, method=RequestMethod.POST)
    public ModelAndView productSearchName(@Valid SampleInputs sampleInputs) {
     ModelAndView model = new ModelAndView();
     String productName = sampleInputs.getName();
     try {
         Product product = productRepository.findByName(productName);
         Set <Element> elements = sortElementsByName(product.getElements());
       	  	Set<ElementQuantity> elementsQuantityList = getElementQuantityList(elements, product);
       	  	model.addObject("msg", "Informacije o proizvodu");
       	  	model.setViewName("home/product_profile");
         model.addObject("product", product);
         model.addObject("elementsList", elements);
         model.addObject("elementsQuantityList", elementsQuantityList);
     } catch(Exception e) {
      	  model.addObject("err", "Nije pronađen proizvod sa nazivom: " + productName);
       	  model.setViewName("home/search_product");
     }
     return model;
    }
    
    // If browser back button is clicked display return to product search beginning
    @RequestMapping(value= {"/home/searchproductname"}, method=RequestMethod.GET)
    public ModelAndView redirectProductNameBackToProductSearch() {
        ModelAndView model = new ModelAndView();
        SampleInputs sampleInputs = new SampleInputs();
        model.addObject("sampleInputs", sampleInputs);
        model.setViewName("home/search_product");
   	  return model;
    }
    
    @RequestMapping(value= {"admin/searchelementsifra"}, method=RequestMethod.POST)
    public ModelAndView searchElementBySifra(@Valid SampleInputs sampleInputs) {
     ModelAndView model = new ModelAndView();
     Set <Element> newElements = new HashSet<Element>();
    	 Element element = elementRepository.findBySifra(sampleInputs.getSifra());
      	Product product = productRepository.findById(sampleInputs.getId()).get();
      	Set<Element> elements = sortElementsByName(product.getElements());
      	System.out.println("ELEMENTS: " + elements.isEmpty());
      	Boolean empty = elements.isEmpty();
    	 if(element == null) {
        	 model.addObject("err", "Nije pronađen element sa šifrom: " + sampleInputs.getSifra());
             model.addObject("elementsList", elements);
             model.addObject("empty", empty);
    	 	}
    	 else if(elementAlreadyInProduct(element, elements)){
        	 model.addObject("err", "Pronađen element sa šifrom: '" + sampleInputs.getSifra() + "', ali već postoji u proizvodu.");
             model.addObject("elementsList", elements);
    	 } else {
    	 		newElements.add(element);
    	 		for(Element item: elements) {
    	 			newElements.add(item);
    	 		}
    	 		model.addObject("elementsList", newElements);
    	 		model.addObject("msg", "Pronađen element sa šifrom: " + sampleInputs.getSifra());		
    	 	}
          model.addObject("product", product);
          model.addObject("sampleInputs", sampleInputs);
          model.setViewName("admin/add_elements");
     return model;
    }

	@RequestMapping(value= {"home/elementsearchkeyword"}, method=RequestMethod.POST)
    public ModelAndView elementSearchKeyWord(@Valid SampleInputs sampleInputs) {
     ModelAndView model = new ModelAndView();
     String keyWord = sampleInputs.getKeyWord();
   	Product product = productRepository.findById(sampleInputs.getId()).get();
         List<Element> foundElements = elementRepository.findByNameContaining(keyWord);
         Set<Element> elements = sortElementsByName(product.getElements());
         if(foundElements.isEmpty()) {
         	  model.addObject("err", "Nije pronađen element koji sadrži ključnu riječ: " + keyWord);
              model.addObject("elementsList", elements);
         	} else {
         		List<Element> selectionElements = new ArrayList<>();
         		foundElements.removeAll(elements);
         		if(foundElements.isEmpty()) {
             		model.addObject("msg", "Lista elemenata koji sadrže ključnu riječ: '" + keyWord + "' se već nalaze u proizvodu.");
         			} else {
         				model.addObject("msg", "Lista elemenata koji sadrže ključnu riječ: " + keyWord);
             			for(Element item: foundElements) {
             				selectionElements.add(item);
             				}
         				}
    	 				for(Element item: elements) {
    	 					selectionElements.add(item);
    	 					}
      			model.addObject("elementsList", selectionElements);         		
         		}
        model.addObject("product", product);
        model.addObject("sampleInputs", sampleInputs);         
  			model.setViewName("admin/add_elements");
         	return model;
    	}
    
    
    @RequestMapping(value= {"home/searchelement"}, method=RequestMethod.POST)
    public ModelAndView searchElementSifra(@Valid SampleInputs sampleInputs, HttpServletRequest request) {
     ModelAndView model = new ModelAndView();
     
     int page = 0; //default page number is 0
     int size = 10; //default page size is 10

     if (request.getParameter("page") != null && !request.getParameter("page").isEmpty()) {
    	 page = Integer.parseInt(request.getParameter("page")) - 1;
     	}

     if (request.getParameter("size") != null && !request.getParameter("size").isEmpty()) {
    	 size = Integer.parseInt(request.getParameter("size"));
     	}
     
     Page <Element> elements = null;
    	 elements = elementRepository.findBySifra(sampleInputs.getSifra(), PageRequest.of(page, size, Sort.by("id").descending()));
    	 if(elements.isEmpty()) {
        	 model.addObject("err", "Nije pronađen element sa šifrom: " + sampleInputs.getSifra());
    	 	} else {
           	 model.addObject("msg", "Pronađen element sa šifrom: " + sampleInputs.getSifra());		
    	 		}
	 		model.addObject("elementsList", elements);    	 
          model.addObject("sampleInputs", new SampleInputs());
          model.setViewName("home/list_elements");
     return model;
    }
	 
    // SEARCH ELEMENTS BY SIFRA
	 @RequestMapping(value= {"home/searchelements"}, method=RequestMethod.POST)
	 public String searchPostsBySifra(@Valid SampleInputs sampleInputs, HttpServletRequest request) {
	     keyWord = sampleInputs.getKeyWord();
	    elementsByKeyWord = elementRepository.findByNameContaining(keyWord);
		 return "redirect:/home/searchelements";
	 }
  
	 /*
    @RequestMapping(value= {"home/searchelements"}, method=RequestMethod.POST)
    public ModelAndView elementsSearchKeyWord(@Valid SampleInputs sampleInputs, HttpServletRequest request) {
     ModelAndView model = new ModelAndView();
     String keyWord = sampleInputs.getKeyWord();
     
     int page = 0; //default page number is 0
     int size = 10; //default page size is 10

     if (request.getParameter("page") != null && !request.getParameter("page").isEmpty()) {
    	 page = Integer.parseInt(request.getParameter("page")) - 1;
     	}

     if (request.getParameter("size") != null && !request.getParameter("size").isEmpty()) {
    	 size = Integer.parseInt(request.getParameter("size"));
     	}

     	Page <Element> elementsList = null;
     	elementsList = elementRepository.findByNameContaining(keyWord, PageRequest.of(page, size, Sort.by("id").descending()));
     
         if(elementsList.isEmpty()) {
         	  model.addObject("err", "Nije pronađen element koji sadrži ključnu riječ: " + keyWord);
         	} else {
         		model.addObject("msg", "Lista elemenata koji sadrže ključnu riječ: " + keyWord);
         		}
			model.addObject("elementsList", elementsList);         		
			model.addObject("sampleInputs", new SampleInputs());         
  			model.setViewName("home/list_elements");
         	return model;
    	}
    */
    // DISPLAY SEARCH RESULTS OF ELEMENTS BY KEY WORD
    @RequestMapping(value= {"home/searchelements"}, method=RequestMethod.GET)
	 public ModelAndView displayPosts(HttpServletRequest request) {
		 ModelAndView model = new ModelAndView();
		 if(elementsByKeyWord.isEmpty()) {
			 	model.addObject("err", "Nije pronađen element koji sadrži ključnu riječ: " + keyWord);
		 } else {
      			model.addObject("msg", "Lista elemenata koji sadrže ključnu riječ: " + keyWord);
		 }
		  	   
		 int page = 0; //default page number is 0
		 int size = 10; //default page size is 10
		       
		 if (request.getParameter("page") != null && !request.getParameter("page").isEmpty()) {
		     page = Integer.parseInt(request.getParameter("page")) - 1;
		  }

		 if (request.getParameter("size") != null && !request.getParameter("size").isEmpty()) {
		     size = Integer.parseInt(request.getParameter("size"));
		  }

		Page <Element> elementsList = null;
		   elementsList = elementRepository.findByNameContaining(keyWord, PageRequest.of(page, size, Sort.by("id").descending()));

        SampleInputs sampleInputs = new SampleInputs();
   		model.addObject("sampleInputs", sampleInputs);
	  	model.addObject("elementsList", elementsList);
	  	model.setViewName("/home/list_elements_keyword");
	  	return model;
    }
    
    private Set<ElementQuantity> getElementQuantityList(Set<Element> elementList, Product product) {
   	 Set<ElementQuantity> elementQuantitiyList = new HashSet<ElementQuantity>();
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
    
    private boolean elementAlreadyInProduct(Element element, Set<Element> elements) {
		for(Element check: elements) {
			if(check.getId() == element.getId()) {
				return true;
			}
		}
		return false;
	}
    
	private Set<Element> sortElementsByName(Set<Element> elements) {
		Set<Element> sortedElements = new TreeSet<>(new ElementNameSorter());
   	  	for(Element element: elements) {
   	  		sortedElements.add(element);
   	  	}
		return sortedElements;
	}

}