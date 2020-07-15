package com.avlija.furniture.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.validation.Valid;

import com.avlija.furniture.form.SampleInputs;
import com.avlija.furniture.model.Element;
import com.avlija.furniture.model.ElementQuantity;
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
public class EditController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UnitMeasureRepository unitMeasureRepository;

    @Autowired
    private ElementRepository elementRepository;
    
    @Autowired
    private ElementQuantityRepository elementQuantityRepository;
    
    // EDIT ELEMENT
    
    @RequestMapping(value= {"admin/editelement/{id}"}, method=RequestMethod.GET)
    public ModelAndView editElement(@PathVariable(name = "id") Integer id) {
     ModelAndView model = new ModelAndView();
     Element element = elementRepository.findById(id).get();
     List<UnitMeasure> unitList = unitMeasureRepository.findAll();
     model.addObject("element", element);
     model.addObject("unitList", unitList);
     model.setViewName("admin/edit_element");
     
     return model;
    }
    
    @RequestMapping(value= {"admin/editelement"}, method=RequestMethod.POST)
    public ModelAndView createElement(@Valid Element element, BindingResult bindingResult) {
     ModelAndView model = new ModelAndView();
     Element oldElement = elementRepository.findById(element.getId()).get();
     Element elementExists = elementRepository.findBySifra(element.getSifra());
     if(elementExists != null) {
    		 bindingResult.rejectValue("sifra", "error.sifra", "Ova šifra već postoji!");
    	     System.out.println("TEST 3");
     }
     if(bindingResult.hasErrors()) {
      model.setViewName("admin/edit_element");
     } else {
    	 oldElement = element;
   	  	elementRepository.save(oldElement);
   	  model.addObject("msg", element.getName() + " je uspješno izmjenjen!");
   	  model.setViewName("admin/edit_element");
     	}
     List<UnitMeasure> unitList = unitMeasureRepository.findAll();
     model.addObject("element", oldElement);
     model.addObject("unitList", unitList);
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