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
import com.avlija.furniture.model.UnitMeasure;
import com.avlija.furniture.repository.ElementQuantityRepository;
import com.avlija.furniture.repository.ElementRepository;
import com.avlija.furniture.repository.OrderRepository;
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
     List<Element> elementsList = product.getElements();
     List<ElementQuantity> elementsQuantityList = getElementQuantityList(elementsList, product);
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
     List <Element> elements = new ArrayList<Element>();
     	Product createdProduct = productRepository.findById(sampleInputs.getPrdId()).get();
        elements = createdProduct.getElements();
        int orderQuantity = sampleInputs.getQuantity();
        List<ElementQuantity> elementsQuantityList = getElementQuantityList(elements, createdProduct);
        List<Integer> totals = new ArrayList<>();
        for(ElementQuantity elementQuantity: elementsQuantityList) {
        	int totalElementQuantity = orderQuantity * elementQuantity.getQuantity();
        	totals.add(totalElementQuantity);
        //	for(Element element: elements) {
        //		int newElementQuantity = element.getQuantity() - totalElementQuantity;
        //		element.setQuantity(newElementQuantity);
        //		elementRepository.save(element);
        //	}
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
     List <Element> elements = new ArrayList<Element>();
     	Product createdProduct = productRepository.findById(sampleInputs.getPrdId()).get();
        elements = createdProduct.getElements();
        int orderQuantity = sampleInputs.getQuantity();
        Order order = new Order(new Date(), orderQuantity, createdProduct);
        orderRepository.save(order);
        
        List<ElementQuantity> elementsQuantityList = getElementQuantityList(elements, createdProduct);
        List<Integer> totals = new ArrayList<>();
        for(ElementQuantity elementQuantity: elementsQuantityList) {
        	int totalElementQuantity = orderQuantity * elementQuantity.getQuantity();
        	totals.add(totalElementQuantity);
        	for(Element element: elements) {
        		int newElementQuantity = element.getQuantity() - totalElementQuantity;
        		element.setQuantity(newElementQuantity);
        		elementRepository.save(element);
        	}
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
    /*
    @RequestMapping(value= {"admin/addelement"}, method=RequestMethod.POST)
    public ModelAndView addElementToProduct(@Valid Product product) {
     ModelAndView model = new ModelAndView();
     List <Element> elements = new ArrayList<Element>();
     elements = product.getElements();
     	Product createdProduct = productRepository.findById(product.getId()).get();
     	for(Element element: elements) {
     		ProductElement productElement = new ProductElement(createdProduct.getId(), element.getId());
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
    

    
    */
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