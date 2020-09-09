package com.avlija.furniture.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

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
 
    @Autowired
    private PipelineRepository pipelineRepository;

    @Autowired
    private ProductQuantityRepository productQuantityRepository;
    
    
    // CREATE ORDER
    
    @RequestMapping(value= {"admin/createorder/{id}"}, method=RequestMethod.GET)
    public ModelAndView createOrder(@PathVariable(name = "id") Integer id) {
     ModelAndView model = new ModelAndView();
     Pipeline pipeline = pipelineRepository.findById(id).get();
     Set<Product> productsList = pipeline.getProducts();
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
     Set <Product> products = new TreeSet<>();
     	Pipeline createdPipeline = pipelineRepository.findById(sampleInputs.getPipelineId()).get();
        products = createdPipeline.getProducts();
        List<ProductQuantity> productsQuantityList = getProductQuantityList(products, createdPipeline);
        Order order = new Order(new Date(), sampleInputs.getWorkPosition(), createdPipeline, sampleInputs.getOrderComment(), sampleInputs.getOrderPackaging(), sampleInputs.getOrderPrep());
       orderRepository.save(order);
       sampleInputs.setOrderId(order.getId());
        // List<Integer> totals = new ArrayList<>();
       // for(ElementQuantity elementQuantity: elementsQuantityList) {
       // 	int totalElementQuantity = orderQuantity * elementQuantity.getQuantity();
       // 	totals.add(totalElementQuantity);
       // }
        
   	  model.addObject("msg", "Kreirani radni nalog spreman za potvrdu");
   	  model.setViewName("admin/confirm_order");
     model.addObject("order", order);
     model.addObject("products", products);
     model.addObject("productsQuantityList", productsQuantityList);
     model.addObject("sampleInputs", sampleInputs);
     //model.addObject("totals", totals);
     return model;
    }
    
    @RequestMapping(value= {"admin/confirmorder"}, method=RequestMethod.POST)
    public ModelAndView confirmOrder(@Valid SampleInputs sampleInputs) {
     ModelAndView model = new ModelAndView();
     Set <Product> products = new TreeSet<>();
     Order order = orderRepository.findById(sampleInputs.getOrderId()).get();
     	Pipeline createdPipeline = order.getPipeline();
        products = createdPipeline.getProducts();
        List<ProductQuantity> productsQuantityList = getProductQuantityList(products, createdPipeline);
        List<Integer> totals = new ArrayList<>();

        for(Product product: products) {
        	PipelineProduct pipelineProduct = new PipelineProduct(createdPipeline.getId(), product.getId());
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
   	  	model.addObject("order", order);
   	  	model.addObject("productsList", products);
   	  	model.addObject("productsQuantityList", productsQuantityList);
   	  	model.addObject("totals", totals);
   	  	model.addObject("order", order);   
     return model;
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
    
    
}