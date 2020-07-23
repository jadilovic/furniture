package com.avlija.furniture.controller;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
import com.avlija.furniture.form.LocalDateAttributeConverter;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class DisplayController {

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
    
    // DISPLAY ALL ELEMENTS
    
    @RequestMapping(value= {"home/allelements"}, method=RequestMethod.GET)
    public ModelAndView allElements() {
     ModelAndView model = new ModelAndView();
     List<Element> elementsList = elementRepository.findAll();
     model.addObject("sampleInputs", new SampleInputs());
     model.addObject("elementsList", elementsList);
     model.setViewName("home/list_elements");
     
     return model;
    }
    
    
    // DISPLAY ALL PRODUCTS
    
    @RequestMapping(value= {"home/allproducts"}, method=RequestMethod.GET)
    public ModelAndView allProducts() {
     ModelAndView model = new ModelAndView();
     List<Product> productsList = productRepository.findAll();
     model.addObject("productsList", productsList);
     model.setViewName("home/list_products");
     
     return model;
    }
    
    @RequestMapping(value= {"home/productprofile/{id}"}, method=RequestMethod.GET)
    public ModelAndView productProfile(@PathVariable(name = "id") Integer id) {
     ModelAndView model = new ModelAndView();
     Product product = productRepository.findById(id).get();
     List <Element> elements = new ArrayList<Element>();
     elements = product.getElements();
   	  	List<ElementQuantity> elementsQuantityList = getElementQuantityList(elements, product);
   	  model.addObject("msg", "Informacije o proizvodu");
   	  model.setViewName("admin/create_product2");
     model.addObject("product", product);
     model.addObject("elementsList", elements);
     model.addObject("elementsQuantityList", elementsQuantityList);
     return model;
    }
    
    // DISPLAY ALL ORDERS
    
    @RequestMapping(value= {"home/allorders"}, method=RequestMethod.GET)
    public ModelAndView allOrders() {
     ModelAndView model = new ModelAndView();
     List<Order> ordersList = orderRepository.findAll();
     model.addObject("message", "Lista radnih naloga");     
     model.addObject("sampleInputs", new SampleInputs());
     model.addObject("ordersList", ordersList);
     model.setViewName("home/list_orders");
     
     return model;
    }
    
    @RequestMapping(value= {"home/orderprofile/{id}"}, method=RequestMethod.GET)
    	public ModelAndView orderProfile(@PathVariable(name = "id") Long id) {
     ModelAndView model = new ModelAndView();
     List <Element> elements = new ArrayList<Element>();
     Order order = orderRepository.findById(id).get();
     	Product createdProduct = productRepository.findById(order.getProduct().getId()).get();
        elements = createdProduct.getElements();
        int orderQuantity = order.getOrderQuant();
        
        List<ElementQuantity> elementsQuantityList = getElementQuantityList(elements, createdProduct);
        List<Integer> totals = new ArrayList<>();
        for(ElementQuantity elementQuantity: elementsQuantityList) {
        	int totalElementQuantity = orderQuantity * elementQuantity.getQuantity();
        	totals.add(totalElementQuantity);
        }
        
   	  	model.addObject("msg", "Profil Radnog Naloga");
   	  	model.setViewName("admin/order_profile");
   	  	model.addObject("product", createdProduct);
   	  	model.addObject("elementsList", elements);
   	  	model.addObject("elementsQuantityList", elementsQuantityList);
   	  	model.addObject("totals", totals);
   	  	model.addObject("order", order);   
     return model;
    }
    
    @RequestMapping(value= {"home/ordersearchid"}, method=RequestMethod.POST)
    public ModelAndView orderSearchId(@Valid SampleInputs sampleInputs) {
     ModelAndView model = new ModelAndView();
     Long orderId = sampleInputs.getId().longValue();
     try {
    	 Order order = orderRepository.findById(orderId).get();
         Product product = productRepository.findById(order.getProduct().getId()).get();
         List <Element> elements = new ArrayList<Element>();
         elements = product.getElements();
       	  	List<ElementQuantity> elementsQuantityList = getElementQuantityList(elements, product);
            List<Integer> totals = new ArrayList<>();
            int orderQuantity = order.getOrderQuant();
            for(ElementQuantity elementQuantity: elementsQuantityList) {
            	int totalElementQuantity = orderQuantity * elementQuantity.getQuantity();
            	totals.add(totalElementQuantity);
            	}    
       	  	model.addObject("msg", "Profil Radnog Naloga");
       	  	model.setViewName("admin/order_profile");
       	  	model.addObject("product", product);
       	  	model.addObject("elementsList", elements);
       	  	model.addObject("elementsQuantityList", elementsQuantityList);
       	  	model.addObject("totals", totals);
       	  	model.addObject("order", order); 
     } catch(Exception e) {
      	  model.addObject("err", "Nije pronađen radni nalog sa ID brojem: " + sampleInputs.getId());
          List<Order> ordersList = orderRepository.findAll();
          model.addObject("message", "Lista radnih naloga");     
          model.addObject("sampleInputs", new SampleInputs());
          model.addObject("ordersList", ordersList);
          model.setViewName("home/list_orders");
     }
     return model;
    }
    
    
    @RequestMapping(value= {"home/ordersearchprdid"}, method=RequestMethod.POST)
    public ModelAndView productSearchKeyWord(@Valid SampleInputs sampleInputs) {
     ModelAndView model = new ModelAndView();
     int productId = sampleInputs.getId();
     try {
         Product product = productRepository.findById(productId).get();
         List<Order> ordersList = orderRepository.findByProduct(product);
         if(ordersList.isEmpty()) {
         	  model.addObject("err", "Nije pronađen radni nalog koji sadrži ID proizvoda: " + productId);
         	} else {
         		model.addObject("message", "Lista radnih naloga sa ID proizvodom: " + productId);
         		model.addObject("ordersList", ordersList);
         		}
     } catch (Exception e) {
     	  model.addObject("err", "Nije pronađen proizvod sa ID brojem: " + sampleInputs.getId());
          List<Order> ordersList = orderRepository.findAll();
          model.addObject("message", "Lista radnih naloga");     
          model.addObject("sampleInputs", new SampleInputs());
          model.addObject("ordersList", ordersList);
     }
     model.setViewName("home/list_orders");
         	return model;
    	}
    
    @RequestMapping(value = "home/searchorderdate", method = RequestMethod.POST)
    public ModelAndView searchByDate(@ModelAttribute("command") SampleInputs sampleInputs) throws ParseException {
        ModelAndView model = new ModelAndView("home/list_orders");

        String inputSearchDate = sampleInputs.getSearchDate();
        
        System.out.println("TEST 1" + inputSearchDate);
        List<Order> orders = searchByDate(inputSearchDate);

   	 if(orders.size() == 0) {
    	  model.addObject("err", "Nije pronađen radni nalog sa datumom: " + inputSearchDate);
          List<Order> ordersList = orderRepository.findAll();
          model.addObject("message", "Lista radnih naloga");     
          model.addObject("ordersList", ordersList);
   	 	} else {
     		model.addObject("message", "Lista radnih naloga na datum: " + inputSearchDate);
     		model.addObject("ordersList", orders);
   	 	}
   	 	model.addObject("sampleInputs", new SampleInputs());
        return model;    
    }
    
    public List<Order> searchByDate(String inputSearchDate) {
   	 DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
   	 LocalDate dateInput = LocalDate.parse(inputSearchDate, formatter);
   		 
   		 System.out.println("TEST 1, TEST 1, TEST 1");
   		 System.out.println("Check in: " + dateInput.toString());
   		 
   		 LocalDateAttributeConverter converter = new LocalDateAttributeConverter();
   		 java.sql.Date date = converter.convertToDatabaseColumn(dateInput);
   		 
   		 System.out.println("TEST 1.5, TEST 1.5, TEST 1.5");
   		 System.out.println("Check in: " + date.toString());
   		 
   		 List<Order> ordersOnDate = new ArrayList<>();
   		 ordersOnDate = orderRepository.findByCreated(date);
   		return ordersOnDate;
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