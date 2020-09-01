package com.avlija.furniture.controller;

import java.util.ArrayList;
import java.util.List;

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
import com.avlija.furniture.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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
         List <Element> elements = new ArrayList<Element>();
         elements = product.getElements();
       	  	List<ElementQuantity> elementsQuantityList = getElementQuantityList(elements, product);
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
    
    
    @RequestMapping(value= {"home/searchproductname"}, method=RequestMethod.POST)
    public ModelAndView productSearchName(@Valid SampleInputs sampleInputs) {
     ModelAndView model = new ModelAndView();
     String productName = sampleInputs.getName();
     try {
         Product product = productRepository.findByName(productName);
         List <Element> elements = new ArrayList<Element>();
         elements = product.getElements();
       	  	List<ElementQuantity> elementsQuantityList = getElementQuantityList(elements, product);
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
    
    @RequestMapping(value= {"admin/searchelementsifra"}, method=RequestMethod.POST)
    public ModelAndView searchElemntBySifra(@Valid SampleInputs sampleInputs) {
     ModelAndView model = new ModelAndView();
     List <Element> newElements = new ArrayList<Element>();
    	 Element element = elementRepository.findBySifra(sampleInputs.getSifra());
      	Product product = productRepository.findById(sampleInputs.getId()).get();
      	List<Element> elements = product.getElements();
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
         List<Element> elements = product.getElements();
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
    public ModelAndView searchElementSifra(@Valid SampleInputs sampleInputs) {
     ModelAndView model = new ModelAndView();
     List <Element> elements = new ArrayList<Element>();
    	 Element element = elementRepository.findBySifra(sampleInputs.getSifra());
    	 if(element == null) {
        	 model.addObject("err", "Nije pronađen element sa šifrom: " + sampleInputs.getSifra());
    	 	} else {
    	 		elements.add(element);
           	 model.addObject("msg", "Pronađen element sa šifrom: " + sampleInputs.getSifra());		
    	 		}
	 		model.addObject("elementsList", elements);    	 
          model.addObject("sampleInputs", new SampleInputs());
          model.setViewName("home/list_elements");
     return model;
    }
    
    @RequestMapping(value= {"home/searchelements"}, method=RequestMethod.POST)
    public ModelAndView elementsSearchKeyWord(@Valid SampleInputs sampleInputs) {
     ModelAndView model = new ModelAndView();
     String keyWord = sampleInputs.getKeyWord();
         List<Element> elementsList = elementRepository.findByNameContaining(keyWord);
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
    
    private boolean elementAlreadyInProduct(Element element, List<Element> elements) {
		for(Element check: elements) {
			if(check.getId() == element.getId()) {
				return true;
			}
		}
		return false;
	}

}