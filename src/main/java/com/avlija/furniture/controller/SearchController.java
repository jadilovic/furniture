package com.avlija.furniture.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;

import com.avlija.furniture.form.SampleInputs;
import com.avlija.furniture.model.Element;
import com.avlija.furniture.model.ElementQuantity;
import com.avlija.furniture.model.Order;
import com.avlija.furniture.model.Product;
import com.avlija.furniture.model.ProductElement;
import com.avlija.furniture.model.UnitMeasure;
import com.avlija.furniture.repository.ElementQuantityRepository;
import com.avlija.furniture.repository.ElementRepository;
import com.avlija.furniture.repository.OrderRepository;
import com.avlija.furniture.repository.ProductRepository;
import com.avlija.furniture.repository.UnitMeasureRepository;
import com.avlija.furniture.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
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
      	  model.addObject("msg", "Nije pronađen proizvod sa ID brojem: " + productId);
       	  model.setViewName("home/search_product");
     }
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
}