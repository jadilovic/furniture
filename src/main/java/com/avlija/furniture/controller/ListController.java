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
    private UnitMeasureRepository unitMeasureRepository;

    @Autowired
    private ElementRepository elementRepository;
    
    @Autowired
    private ElementQuantityRepository elementQuantityRepository;


    // CREATE LIST OF PRODUCTS
    
    @RequestMapping(value= {"admin/createlist"}, method=RequestMethod.GET)
    public ModelAndView createProduct() {
     ModelAndView model = new ModelAndView();
     ListOfProducts listOfProducts = new ListOfProducts();
     model.addObject("listOfProducts", listOfProducts);
     model.setViewName("admin/create_list");
     
     return model;
    }
    
    @RequestMapping(value= {"admin/createlist"}, method=RequestMethod.POST)
    public ModelAndView createProduct(@Valid Product product, BindingResult bindingResult) {
     ModelAndView model = new ModelAndView();
     Product productExists = productRepository.findByName(product.getName());
     Product productExists2 = productRepository.findByProductSize(product.getProductSize());
     if(productExists != null && productExists2 != null) {
    		 bindingResult.rejectValue("name", "error.name", "Ovaj naziv proizvoda i mjera već postoji!");
     }
     if(bindingResult.hasErrors()) {
      	  model.addObject("msg", "Uneseni naziv proizvoda i mjere već postoji.");

      model.setViewName("admin/create_product");
     } else {
   	  	productRepository.save(product);
   	  model.addObject("msg", "Naziv novog proizvoda je uspješno kreiran! Potrebno je dodati elemente od kojih se sastoji proizvod.");
   	  model.setViewName("admin/create_product2");
     	}
     Product newProduct = productRepository.findByName(product.getName());
     List<Element> elements = new ArrayList<>();
     model.addObject("elementsList", elements);
     model.addObject("product", newProduct);
     return model;
    }
    
    @RequestMapping(value= {"admin/addelement/{id}"}, method=RequestMethod.GET)
    public ModelAndView addElement(@PathVariable(name = "id") Integer id) {
     ModelAndView model = new ModelAndView();
     Product product = productRepository.findById(id).get();
     SampleInputs sampleInputs = new SampleInputs();
     sampleInputs.setId(id);
     model.addObject("product", product);
     model.addObject("sampleInputs", sampleInputs);
     model.addObject("elementsList", product.getElements());
     model.setViewName("admin/add_elements");
     
     return model;
    }
    
    @RequestMapping(value= {"admin/addelement"}, method=RequestMethod.POST)
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
    
}