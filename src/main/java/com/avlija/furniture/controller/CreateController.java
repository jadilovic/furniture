package com.avlija.furniture.controller;

import java.util.List;

import javax.validation.Valid;

import com.avlija.furniture.model.Element;
import com.avlija.furniture.model.UnitMeasure;
import com.avlija.furniture.repository.ElementRepository;
import com.avlija.furniture.repository.UnitMeasureRepository;
import com.avlija.furniture.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class CreateController {

    @Autowired
    private UserService userService;

    @Autowired
    private UnitMeasureRepository unitMeasureRepository;

    @Autowired
    private ElementRepository elementRepository;
    
    // CREATE UNIT OF MEASUREMENT
    
    @RequestMapping(value= {"admin/createmeasure"}, method=RequestMethod.GET)
    public ModelAndView createMeasure() {
     ModelAndView model = new ModelAndView();
     UnitMeasure unitMeasure = new UnitMeasure();
     model.addObject("unitMeasure", unitMeasure);
     model.setViewName("admin/create_measure");
     
     return model;
    }
    
    @RequestMapping(value= {"admin/createmeasure"}, method=RequestMethod.POST)
    public ModelAndView createUnitMeasure(@Valid UnitMeasure unitMeasure, BindingResult bindingResult) {
     ModelAndView model = new ModelAndView();
     UnitMeasure unitMeasureExists = unitMeasureRepository.findByName(unitMeasure.getName());
     
     if(unitMeasureExists != null) {
      bindingResult.rejectValue("name", "error.unitMeasure", "Ova jedinica mjere već postoji!");
     }
     if(bindingResult.hasErrors()) {
      model.setViewName("admin/create_measure");
     } else {
   	  	unitMeasureRepository.save(unitMeasure);
   	  model.addObject("msg", "Nova jedinica mjere je uspješno kreirana!");
   	  model.addObject("unitMeasure", new UnitMeasure());
   	  model.setViewName("admin/create_measure");
     	}
     return model;
    }
    
    // CREATE AN ELEMENT
    
    @RequestMapping(value= {"admin/createelement"}, method=RequestMethod.GET)
    public ModelAndView createElement() {
     ModelAndView model = new ModelAndView();
     Element element = new Element();
     List<UnitMeasure> unitList = unitMeasureRepository.findAll();
     model.addObject("element", element);
     model.addObject("unitList", unitList);
     model.setViewName("admin/create_element");
     
     return model;
    }
    
    @RequestMapping(value= {"admin/createelement"}, method=RequestMethod.POST)
    public ModelAndView createElement(@Valid Element element, BindingResult bindingResult) {
     ModelAndView model = new ModelAndView();
     Element elementExists = elementRepository.findBySifra(element.getSifra());
     
     if(elementExists != null) {
    		 bindingResult.rejectValue("sifra", "error.sifra", "Ova šifra već postoji!");
     }
     if(bindingResult.hasErrors()) {
      model.setViewName("admin/create_element");
     } else {
   	  	elementRepository.save(element);
   	  model.addObject("msg", "Novi element je uspješno kreiran!");
   	  model.addObject("element", new Element());
   	  model.setViewName("admin/create_element");
     	}
     return model;
    }
}