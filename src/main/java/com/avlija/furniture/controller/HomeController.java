package com.avlija.furniture.controller;

import javax.validation.Valid;

import com.avlija.furniture.model.User;
import com.avlija.furniture.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomeController {

    @Autowired
    private UserService userService;

    
    @RequestMapping(value={"/admin/adminPage"}, method = RequestMethod.GET)
    public ModelAndView adminPage(){
        ModelAndView modelAndView = new ModelAndView();
        User user = getCurrentUser();
        modelAndView.addObject("userName", user.getUserName());
        modelAndView.setViewName("admin/adminPage");
        return modelAndView;
    }

    private User getCurrentUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByUserName(auth.getName());
		return user;
}
}