package com.avlija.furniture.controller;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.avlija.furniture.form.SampleInputs;
import com.avlija.furniture.model.Element;
import com.avlija.furniture.model.ElementQuantity;
import com.avlija.furniture.model.Order;
import com.avlija.furniture.model.Pipeline;
import com.avlija.furniture.model.Product;
import com.avlija.furniture.model.ProductElement;
import com.avlija.furniture.repository.ElementQuantityRepository;
import com.avlija.furniture.repository.ElementRepository;
import com.avlija.furniture.repository.OrderRepository;
import com.avlija.furniture.repository.PipelineRepository;
import com.avlija.furniture.repository.ProductRepository;
import com.avlija.furniture.service.ElementNameSorter;
import com.avlija.furniture.form.LocalDateAttributeConverter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class DisplayController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ElementRepository elementRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private ElementQuantityRepository elementQuantityRepository;
    
    @Autowired
    private PipelineRepository pipelineRepository;


    // DISPLAY ALL ELEMENTS
    @RequestMapping(value= {"home/allelements"}, method=RequestMethod.GET)
    public String allElements(HttpServletRequest request, Model model) {
              
         int page = 0; //default page number is 0
         int size = 10; //default page size is 10
         
         if (request.getParameter("page") != null && !request.getParameter("page").isEmpty()) {
             page = Integer.parseInt(request.getParameter("page")) - 1;
         }

         if (request.getParameter("size") != null && !request.getParameter("size").isEmpty()) {
             size = Integer.parseInt(request.getParameter("size"));
         }

         Page <Element> elementsList = null;
         elementsList = elementRepository.findAll(PageRequest.of(page, size, Sort.by("id").descending()));
 
     model.addAttribute("sampleInputs", new SampleInputs());
     model.addAttribute("elementsList", elementsList);
     
     return "home/list_elements";
    }
    
    
    // DISPLAY ALL PRODUCTS
    @RequestMapping(value= {"home/allproducts"}, method=RequestMethod.GET)
    public String allProducts(HttpServletRequest request, Model model) {
        
        int page = 0; //default page number is 0
        int size = 10; //default page size is 10
        
        if (request.getParameter("page") != null && !request.getParameter("page").isEmpty()) {
            page = Integer.parseInt(request.getParameter("page")) - 1;
        }

        if (request.getParameter("size") != null && !request.getParameter("size").isEmpty()) {
            size = Integer.parseInt(request.getParameter("size"));
        }

        Page <Product> productsList = null;
        productsList = productRepository.findAll(PageRequest.of(page, size, Sort.by("id").descending()));

    model.addAttribute("productsList", productsList);   
    return "home/list_products";
    }
    
    // DISPLAY PRODUCT PROFILE
    @RequestMapping(value= {"home/productprofile/{id}"}, method=RequestMethod.GET)
    public ModelAndView productProfile(@PathVariable(name = "id") Integer id) {
     ModelAndView model = new ModelAndView();
     Product product = productRepository.findById(id).get();
     Set <Element> elements = sortElementsByName(product.getElements());
   	  List<ElementQuantity> elementsQuantityList = getElementQuantityList(elements, product);
   	  model.addObject("msg", "Informacije o proizvodu");
   	  model.setViewName("home/product_profile");
     model.addObject("product", product);
     model.addObject("elementsList", elements);
     model.addObject("elementsQuantityList", elementsQuantityList);
     return model;
    }
    
    // SEARCH AND DISPLAY 10 LAST ORDERS
    @RequestMapping(value= {"home/allorders"}, method=RequestMethod.GET)
    public ModelAndView allOrders() {
     ModelAndView model = new ModelAndView();
   // List<Order> ordersList = orderRepository.findAll(Sort.by("created").descending());
     List<Order> ordersList = orderRepository.findFirst10ByOrderCompletedOrderByIdDesc(1);
     model.addObject("message", "Lista radnih naloga");     
     model.addObject("sampleInputs", new SampleInputs());
     model.addObject("ordersList", ordersList);
     model.setViewName("home/list_orders");
     
     return model;
    }
    
    // DISPLAY ALL ORDERS
    @RequestMapping(value= {"home/listallorders"}, method=RequestMethod.GET)
    public String listAllOrdersPage(HttpServletRequest request, Model model) {
        
        int page = 0; //default page number is 0
        int size = 10; //default page size is 10
        
        if (request.getParameter("page") != null && !request.getParameter("page").isEmpty()) {
            page = Integer.parseInt(request.getParameter("page")) - 1;
        }

        if (request.getParameter("size") != null && !request.getParameter("size").isEmpty()) {
            size = Integer.parseInt(request.getParameter("size"));
        }

        Page <Order> ordersList = null;
        ordersList = orderRepository.findAll(PageRequest.of(page, size, Sort.by("id").descending()));

     model.addAttribute("message", "Lista radnih naloga");
     model.addAttribute("ordersList", ordersList);
     
     return "home/list_all_orders";
    }
    
    // DISPLAY AND SEARCH ORDERS BY PIPELINE ID
    @RequestMapping(value= {"home/ordersearchpipelineid"}, method=RequestMethod.POST)
    public ModelAndView orderSearchByPipelineId(@Valid SampleInputs sampleInputs) {
     ModelAndView model = new ModelAndView();
     int pipelineId = sampleInputs.getId();
     try {
         Pipeline pipeline = pipelineRepository.findById(pipelineId).get();
         List<Order> ordersList = orderRepository.findByPipeline(pipeline, Sort.by("created").descending());
         if(ordersList.isEmpty()) {
         	  model.addObject("err", "Nije pronađen radni nalog koji sadrži ID liste proizvoda: " + pipelineId);
         	} else {
         		model.addObject("message", "Lista radnih naloga sa ID proizvodom: " + pipelineId);
         		model.addObject("ordersList", ordersList);
         		}
     } catch (Exception e) {
     	  model.addObject("err", "Nije pronađena lista proizvoda sa ID brojem: " + sampleInputs.getId());
          //List<Order> ordersList = orderRepository.findAll(Sort.by("created").descending());
          List<Order> ordersList = orderRepository.findFirst10ByOrderCompletedOrderByIdDesc(1);
          model.addObject("message", "Lista radnih naloga");     
          model.addObject("sampleInputs", new SampleInputs());
          model.addObject("ordersList", ordersList);
     }
     model.setViewName("home/list_orders");
         	return model;
    	}
    
    // Redirecting user to the beginning of searching orders
    @RequestMapping(value= {"home/ordersearchpipelineid"}, method=RequestMethod.GET)
    public String redirectBackToOrderSearch(HttpServletRequest request) {
   	 return "redirect:/home/allorders";
    }
    
    // DISPLAY AND SEARCH ORDERS BY DATE
    @RequestMapping(value = "home/searchorderdate", method = RequestMethod.POST)
    public ModelAndView searchByDate(@ModelAttribute("command") SampleInputs sampleInputs) throws ParseException {
        ModelAndView model = new ModelAndView("home/list_orders");

        String inputSearchDate = sampleInputs.getSearchDate();
        
        System.out.println("TEST 1" + inputSearchDate);
        List<Order> orders = searchByDate(inputSearchDate);

   	 if(orders.size() == 0) {
    	  model.addObject("err", "Nije pronađen radni nalog sa datumom: " + inputSearchDate);
          // List<Order> ordersList = orderRepository.findAll(Sort.by("created").descending());
          List<Order> ordersList = orderRepository.findFirst10ByOrderCompletedOrderByIdDesc(1);
          model.addObject("message", "Lista radnih naloga");     
          model.addObject("ordersList", ordersList);
   	 	} else {
     		model.addObject("message", "Lista radnih naloga na datum: " + inputSearchDate);
     		model.addObject("ordersList", orders);
   	 	}
   	 	model.addObject("sampleInputs", new SampleInputs());
        return model;    
    }
    
    // Redirecting user to the beginning of searching orders
    @RequestMapping(value= {"home/searchorderdate"}, method=RequestMethod.GET)
    public String redirectBackToSearchingOrders(HttpServletRequest request) {
   	 return "redirect:/home/allorders";
    }
    
    
    // METHOD FOR SEARCHING ORDERS BY ORDER DATE CREATED
    public List<Order> searchByDate(String inputSearchDate) {
   	 DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		 List<Order> ordersOnDate = new ArrayList<>();
		 try {
			 LocalDate dateInput = LocalDate.parse(inputSearchDate, formatter);
			 LocalDateAttributeConverter converter = new LocalDateAttributeConverter();
			 java.sql.Date date = converter.convertToDatabaseColumn(dateInput);
			 ordersOnDate = orderRepository.findByCreatedDate(date);
		 	} catch (Exception e) {
		 		System.out.println("Pogresan unos datuma.");
		 		}
   		return ordersOnDate;
    	}

    
    // GET ELEMENT QUANTITY
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
    
    // SORTING PRODUCTS BY ELEMENT NAME
	private Set<Element> sortElementsByName(Set<Element> elements) {
		Set<Element> sortedElements = new TreeSet<>(new ElementNameSorter());
   	  	for(Element element: elements) {
   	  		sortedElements.add(element);
   	  	}
		return sortedElements;
	}
	   
    // CREATE PRODUCT QUANTITY LIST FOR THE PIPELINE
    /*
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

    // SORTING PRODUCTS IN THE PIPELINE BY PRODUCT ID
    private Set<Product> sortByProductId(Set<Product> products) {
		Set<Product> sortedProducts = new TreeSet<>(new ProductIdComp());
	     for(Product product: products) {
	    	 sortedProducts.add(product);
	     	}
		return sortedProducts;
	}
        */
}