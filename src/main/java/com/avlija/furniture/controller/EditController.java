package com.avlija.furniture.controller;
// EDITING OF ELEMENTS ONLY

import java.util.List;
import javax.validation.Valid;

import com.avlija.furniture.model.Element;
import com.avlija.furniture.model.UnitMeasure;
import com.avlija.furniture.repository.ElementRepository;
import com.avlija.furniture.repository.UnitMeasureRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

// EDITING OF ELEMENT
@Controller
public class EditController {

    @Autowired
    private UnitMeasureRepository unitMeasureRepository;

    @Autowired
    private ElementRepository elementRepository;
    
    private static Element savedElement;

    // START EDITING SELECTED ELEMENT
    @RequestMapping(value= {"admin/editelement/{id}"}, method=RequestMethod.GET)
    public ModelAndView editElement(@PathVariable(name = "id") Integer id) {
     ModelAndView model = new ModelAndView();
     savedElement = elementRepository.findById(id).get();
     List<UnitMeasure> unitList = unitMeasureRepository.findAll();
     model.addObject("element", savedElement);
     model.addObject("unitList", unitList);
     model.setViewName("admin/edit_element");
     return model;
    }
    
    // ENTERING VALUES FOR EDITING THE ELEMENT
    @RequestMapping(value= {"admin/editelement"}, method=RequestMethod.POST)
    public ModelAndView editElement(@Valid Element element, BindingResult bindingResult) {
     ModelAndView model = new ModelAndView();
     Element oldElement = elementRepository.findById(element.getId()).get();
     Element elementExists = elementRepository.findBySifra(element.getSifra());     
     if(!oldElement.getSifra().equals(element.getSifra()) && (elementExists != null)) {
    	 model.addObject("msg", "'" + element.getSifra() + "' već postoji!");
         List<UnitMeasure> unitList = unitMeasureRepository.findAll();
         model.addObject("unitList", unitList);
         model.setViewName("admin/edit_element");
         } else {
        	 oldElement = element;
        	 oldElement.setUnitMeasure(unitMeasureRepository.findById(element.getUnitMeasure().getUnitMeasureId()).get());
   	  		elementRepository.save(oldElement);
   	  		model.addObject("msg", element.getName() + " je uspješno izmjenjen!");
   	  		model.setViewName("home/element_profile");
     	}
	 	Element newElement = elementRepository.findById(element.getId()).get();
	 	model.addObject("element", newElement);
     return model;
    }
    
    // IF BROWSER BACK BUTTON IS PRESSED REDIRECT TO ELEMENT PROFILE
	 @RequestMapping(value= {"admin/editelement"}, method=RequestMethod.GET)
	 public String redirectBackElementProfile() {
		 return "redirect:/home/elementprofile/" + savedElement.getId();
	 }
    
	 // STARTING TO ENTER NEW ELEMENT QUANTITY
    @RequestMapping(value= {"/home/editelementquantity/{id}"}, method=RequestMethod.GET)
    public ModelAndView editQuantity(@PathVariable(name = "id") Integer id) {
     ModelAndView model = new ModelAndView();
     savedElement = elementRepository.findById(id).get();
     model.addObject("element", savedElement);
     model.setViewName("home/edit_element_quantity");
     return model;
    }
    
    // ENTERING NEW ELEMENT QUANTITY
    @RequestMapping(value= {"/home/editelementquantity"}, method=RequestMethod.POST)
    public ModelAndView editQuantity(@Valid Element element) {
     ModelAndView model = new ModelAndView();
     	Element oldElement = elementRepository.findById(element.getId()).get();
    	oldElement.setQuantity(element.getQuantity());
   	  	elementRepository.save(oldElement);
   	  	model.addObject("msg", element.getName() + " količina na stanju je uspješno dopunjena!");
   	  	model.setViewName("home/element_profile");
   	  	model.addObject("element", oldElement);
     return model;
    }
    
    // IF BROWSER BACK BUTTON IS PRESSED REDIRECT TO ELEMENT PROFILE
	 @RequestMapping(value= {"home/editelementquantity"}, method=RequestMethod.GET)
	 public String redirectBackToElementProfile() {
		 return "redirect:/home/elementprofile/" + savedElement.getId();
	 }

    
}