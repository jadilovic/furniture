package com.avlija.furniture.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.validation.Valid;

import com.avlija.furniture.form.SampleInputs;
import com.avlija.furniture.model.Element;
import com.avlija.furniture.model.ElementQuantity;
import com.avlija.furniture.model.Order;
import com.avlija.furniture.model.Product;
import com.avlija.furniture.model.ProductElement;
import com.avlija.furniture.repository.ElementQuantityRepository;
import com.avlija.furniture.repository.ElementRepository;
import com.avlija.furniture.repository.OrderRepository;
import com.avlija.furniture.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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
    
    // CREATE ORDER
    
    @RequestMapping(value= {"admin/createorder/{id}"}, method=RequestMethod.GET)
    public ModelAndView createOrder(@PathVariable(name = "id") Integer id) {
     ModelAndView model = new ModelAndView();
     Product product = productRepository.findById(id).get();
     Set<Element> elementsList = product.getElements();
     Set<ElementQuantity> elementsQuantityList = getElementQuantityList(elementsList, product);
     SampleInputs sampleInputs = new SampleInputs();
     sampleInputs.setPrdId(id);
     sampleInputs.setQuantity(0);
     model.addObject("elementsQuantityList", elementsQuantityList);
     model.addObject("product", product);
     model.addObject("elementsList", elementsList);
     model.addObject("sampleInputs", sampleInputs);
     model.setViewName("admin/create_order");
     return model;
    }
    
    @RequestMapping(value= {"admin/createorder"}, method=RequestMethod.POST)
    public ModelAndView addedElementQuantity(@Valid SampleInputs sampleInputs) {
     ModelAndView model = new ModelAndView();
     Set <Element> elements = new HashSet<Element>();
     	Product createdProduct = productRepository.findById(sampleInputs.getPrdId()).get();
        elements = createdProduct.getElements();
        int orderQuantity = sampleInputs.getQuantity();
        Set<ElementQuantity> elementsQuantityList = getElementQuantityList(elements, createdProduct);
        List<Integer> totals = new ArrayList<>();
        for(ElementQuantity elementQuantity: elementsQuantityList) {
        	int totalElementQuantity = orderQuantity * elementQuantity.getQuantity();
        	totals.add(totalElementQuantity);
        }
        
   	  model.addObject("msg", "Kreirani radni nalog spreman za potvrdu");
   	  model.setViewName("admin/confirm_order");
     model.addObject("product", createdProduct);
     model.addObject("elementsList", elements);
     model.addObject("elementsQuantityList", elementsQuantityList);
     model.addObject("totals", totals);
     return model;
    }
    
    @RequestMapping(value= {"admin/confirmorder"}, method=RequestMethod.POST)
    public ModelAndView confirmOrder(@Valid SampleInputs sampleInputs) {
     ModelAndView model = new ModelAndView();
     Set <Element> elements = new HashSet<Element>();
     	Product createdProduct = productRepository.findById(sampleInputs.getPrdId()).get();
        elements = createdProduct.getElements();
        int orderQuantity = sampleInputs.getQuantity();
        Order order = new Order(new Date(), orderQuantity, createdProduct);
        orderRepository.save(order);
        
        Set<ElementQuantity> elementsQuantityList = getElementQuantityList(elements, createdProduct);
        List<Integer> totals = new ArrayList<>();
        for(ElementQuantity elementQuantity: elementsQuantityList) {
        	int totalElementQuantity = orderQuantity * elementQuantity.getQuantity();
        	totals.add(totalElementQuantity);
        	Element element = elementRepository.findById(elementQuantity.getProductElement().getElementId()).get();
        		int newElementQuantity = element.getQuantity() - totalElementQuantity;
        		element.setQuantity(newElementQuantity);
        		elementRepository.save(element);
        }
        
   	  	model.addObject("msg", "Potvrđen radni nalog");
   	  	model.setViewName("admin/order_profile");
   	  	model.addObject("product", createdProduct);
   	  	model.addObject("elementsList", elements);
   	  	model.addObject("elementsQuantityList", elementsQuantityList);
   	  	model.addObject("totals", totals);
   	  	model.addObject("order", order);   
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

}