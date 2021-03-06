package com.avlija.furniture.controller;

import java.util.Date;

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
public class LoginController {

    @Autowired
    private UserService userService;

    // LOGIN PAGE
    @RequestMapping(value={"/", "/login"}, method = RequestMethod.GET)
    public ModelAndView login(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("login");
        return modelAndView;
    }

    // REGISTRATION OF NEW USER PAGE
    @RequestMapping(value="/registration", method = RequestMethod.GET)
    public ModelAndView registration(){
        ModelAndView modelAndView = new ModelAndView();
        User user = new User();
        user.setRole("CLIENT");
        modelAndView.addObject("user", user);
        modelAndView.setViewName("registration");
        return modelAndView;
    }

    // ENTERING DATA FOR THE NEW USER
    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    public ModelAndView createNewUser(@Valid User user, BindingResult bindingResult) {
        ModelAndView modelAndView = new ModelAndView();
        User userExists = userService.findUserByUserName(user.getUserName());
        if (userExists != null) {
            bindingResult
                    .rejectValue("userName", "error.user",
                            "There is already a user registered with the user name provided");
        }
        if (bindingResult.hasErrors()) {
            modelAndView.setViewName("registration");
        } else {
        	user.setCreated(new Date());
            userService.saveUser(user);
            modelAndView.addObject("successMessage", "Korisnik '" + user.getName() + "' je uspješno registrovan");
            User newUser = new User();
            newUser.setRole("CLIENT");            
            modelAndView.addObject("user", newUser);
            modelAndView.setViewName("registration");
        }
        return modelAndView;
    }

    // STARTING PAGE WHEN USER LOGS IN
    @RequestMapping(value="/home/home", method = RequestMethod.GET)
    public ModelAndView home(){
        ModelAndView modelAndView = new ModelAndView();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByUserName(auth.getName());
        modelAndView.addObject("userName", user.getUserName() + " (" + user.getEmail() + ")");
        modelAndView.setViewName("home/home");
        return modelAndView;
    }

    // PAGE SHOWING WHEN USER HAS NO PERMISSION TO ACCESS THE PAGE
    @RequestMapping(value= {"/access_denied"}, method=RequestMethod.GET)
    public ModelAndView accessDenied() {
     ModelAndView model = new ModelAndView();
     model.setViewName("access_denied");
     return model;
    }

}