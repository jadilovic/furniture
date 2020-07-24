package com.avlija.furniture.controller;

import javax.validation.Valid;

import com.avlija.furniture.model.Element;
import com.avlija.furniture.model.Product;
import com.avlija.furniture.model.User;
import com.avlija.furniture.repository.ElementRepository;
import com.avlija.furniture.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomeController {

    @Autowired
    private UserService userService;

    @Autowired
    private ElementRepository elementRepository;
    
    @RequestMapping(value={"/admin/adminPage"}, method = RequestMethod.GET)
    public ModelAndView adminPage(){
        ModelAndView modelAndView = new ModelAndView();
        User user = getCurrentUser();
        modelAndView.addObject("userName", user.getUserName());
        modelAndView.setViewName("admin/adminPage");
        return modelAndView;
    }
    
    @RequestMapping(value={"/home/clientPage"}, method = RequestMethod.GET)
    public ModelAndView clientPage(){
        ModelAndView modelAndView = new ModelAndView();
        User user = getCurrentUser();
        modelAndView.addObject("userName", user.getUserName());
        modelAndView.setViewName("home/clientPage");
        return modelAndView;
    }
    
    @RequestMapping(value= {"home/elementprofile/{id}"}, method=RequestMethod.GET)
    public ModelAndView addElement(@PathVariable(name = "id") Integer id) {
     ModelAndView model = new ModelAndView();
     Element element = elementRepository.findById(id).get();
     model.addObject("msg", "Profil Odabranog Elementa");
     model.addObject("element", element);
     model.setViewName("home/element_profile");
     
     return model;
    }
    
    @RequestMapping(value={"/home/profile"}, method = RequestMethod.GET)
    public ModelAndView userProfilePage(){
        ModelAndView modelAndView = new ModelAndView();
        User user = getCurrentUser();
        modelAndView.addObject("userProfile", user);
        modelAndView.addObject("msg", "Korisnički profil za " + user.getUserName());
        modelAndView.setViewName("home/profile_page");
        return modelAndView;
    }

    private User getCurrentUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByUserName(auth.getName());
		return user;
}
}