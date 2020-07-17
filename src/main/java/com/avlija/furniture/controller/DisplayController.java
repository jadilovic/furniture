package com.avlija.furniture.controller;

import java.util.List;

import javax.validation.Valid;

import com.avlija.furniture.model.Element;
import com.avlija.furniture.model.Product;
import com.avlija.furniture.model.UnitMeasure;
import com.avlija.furniture.repository.ElementRepository;
import com.avlija.furniture.repository.ProductRepository;
import com.avlija.furniture.repository.UnitMeasureRepository;
import com.avlija.furniture.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class DisplayController {

    @Autowired
    private UserService userService;

    @Autowired
    private UnitMeasureRepository unitMeasureRepository;

    @Autowired
    private ElementRepository elementRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    // DISPLAY ALL ELEMENTS
    
    @RequestMapping(value= {"home/allelements"}, method=RequestMethod.GET)
    public ModelAndView allElements() {
     ModelAndView model = new ModelAndView();
     List<Element> elementsList = elementRepository.findAll();
     System.out.println(elementsList);
     model.addObject("elementsList", elementsList);
     model.setViewName("home/list_elements");
     
     return model;
    }
    
    
    // DISPLAY ALL ELEMENTS
    
    @RequestMapping(value= {"home/allproducts"}, method=RequestMethod.GET)
    public ModelAndView allProducts() {
     ModelAndView model = new ModelAndView();
     List<Product> productsList = productRepository.findAll();
     model.addObject("productsList", productsList);
     model.setViewName("home/list_products");
     
     return model;
    }
    
}