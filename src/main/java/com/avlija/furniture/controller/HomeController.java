package com.avlija.furniture.controller;

import java.util.List;
import java.util.Set;

import javax.validation.Valid;

import com.avlija.furniture.model.Element;
import com.avlija.furniture.model.Product;
import com.avlija.furniture.model.Role;
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
        Set <Role> roles = user.getRoles();
        modelAndView.addObject("userProfile", user);
        modelAndView.addObject("roles", roles);
        modelAndView.addObject("msg", "Korisnički profil za " + user.getUserName());
        modelAndView.setViewName("home/profile_page");
        return modelAndView;
    }
    
    @RequestMapping(value= {"/admin/allusers"}, method=RequestMethod.GET)
    public ModelAndView showAllUsers() {
     ModelAndView model = new ModelAndView();
     List<User> listUsers = userService.findAllUsers();
     model.addObject("listUsers", listUsers);
     User user = new User();
     model.addObject("user", user);
     model.setViewName("admin/list_all_users");
     
     return model;
    }
    
    @RequestMapping(value= {"admin/editprofile/{id}"}, method=RequestMethod.GET)
    public ModelAndView editProfile(@PathVariable(name = "id") Integer id) {
     ModelAndView model = new ModelAndView();
     User user = userService.findUserById(id);
     Set<Role> roles = user.getRoles();
     for(Role role: roles)
   	  user.setRole(role.getRole());
     model.addObject("user", user);
     model.setViewName("admin/edit_user");
     return model;
    }
    
    @RequestMapping(value= {"admin/editprofile"}, method=RequestMethod.POST)
    public ModelAndView editProduct(@Valid User user) {
     ModelAndView model = new ModelAndView();
     
     User changedUser = userService.findUserById(user.getId());
     System.out.println(changedUser.getId() + ", NNNNNN password: " + changedUser.getPassword());
     changedUser.setUserName(user.getUserName());
     changedUser.setName(user.getName());
     changedUser.setLastName(user.getLastName());
     changedUser.setRole(user.getRole());
     changedUser.setActive(user.getActive());
     userService.updateUser(changedUser);
     
     Set<Role> roles = changedUser.getRoles();
     
   	  model.addObject("msg", "Izmjena podataka profila korisnika izvrsena");
   	  model.addObject("userProfile", changedUser);
   	  model.addObject("roles", roles);
   	  model.setViewName("home/profile_page");
    
     return model;
    }

    private User getCurrentUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByUserName(auth.getName());
		return user;
}
}