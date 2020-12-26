package com.avlija.furniture.controller;

import java.util.Date;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.avlija.furniture.form.SampleInputs;
import com.avlija.furniture.model.User;
import com.avlija.furniture.service.EmailService;
import com.avlija.furniture.service.UserService;


@Controller
public class PasswordController {

	// Includes password validation for PASSWORD RESET ACTIVITY
	@Autowired
	private UserService userService;

	@Autowired
	private EmailService emailService;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	// Display forgotPassword page
	@RequestMapping(value = "/forgot", method = RequestMethod.GET)
	public ModelAndView displayForgotPasswordPage() {
		SampleInputs sampleInputs = new SampleInputs();
		ModelAndView model = new ModelAndView();
		model.addObject("sampleInputs", sampleInputs);
		model.setViewName("user/forgotPassword");
		return model;
    }
    
    // Process form submission from forgotPassword page
	// It only works on LOCALHOST
	@RequestMapping(value = "/forgot", method = RequestMethod.POST)
	public ModelAndView processForgotPasswordForm(ModelAndView modelAndView, SampleInputs sampleInputs, HttpServletRequest request) {

		// Lookup user in database by e-mail
		User user = userService.findUserByEmail(sampleInputs.getEmail());

		if (user == null) {
			modelAndView.addObject("message", "Nije pronađen uneseni e-mail.");
			modelAndView.setViewName("user/forgotPassword");
		} else {
			
			// Generate random 36-character string token for reset password 
			// User user = optional.get();
			user.setResetToken(UUID.randomUUID().toString());

			// Save token to database
			userService.resetUpdate(user);

			String appUrl = request.getScheme() + "://" + request.getServerName();
			
			// Email message
			SimpleMailMessage passwordResetEmail = new SimpleMailMessage();
			passwordResetEmail.setFrom("yap.webapp@gmail.com");
			passwordResetEmail.setTo(user.getEmail());
			passwordResetEmail.setSubject("Zahtjev za izmjenu lozinke - Password Reset Request" + new Date().toString());
			passwordResetEmail.setText("Za izmjenu lozinke kliknite na donji link:\n" + appUrl
					+ ":8080/reset?token=" + user.getResetToken());
			
			emailService.sendEmail(passwordResetEmail);

			// Add success message to view
			modelAndView.addObject("message", "Link za izmjenu lozinke je poslan na " + sampleInputs.getEmail()
										+ ".\n Otvorite vaš e-mail i kliknite na link.");
			modelAndView.setViewName("user/login");
		}
		return modelAndView;

	}

	// Display form to reset password
	@RequestMapping(value = "/reset", method = RequestMethod.GET)
	public ModelAndView displayResetPasswordPage(ModelAndView modelAndView, @RequestParam("token") String token) {
		
		User user = userService.findUserByResetToken(token);
		SampleInputs sampleInputs = new SampleInputs();

		if (user != null) { // Token found in DB
			sampleInputs.setToken(token);
		} else { // Token not found in DB
			modelAndView.addObject("errorMessage", "Oops!  Ovaj link je istekao. Token nije pronađen u bazi podataka");
		}
		
		modelAndView.addObject("sampleInputs", sampleInputs);
		modelAndView.setViewName("user/resetPassword");
		return modelAndView;
	}

	// Process reset password form
	@RequestMapping(value = "/reset", method = RequestMethod.POST)
	public ModelAndView setNewPassword(ModelAndView modelAndView, SampleInputs sampleInputs) {

		// Find the user associated with the reset token
		System.out.println("TOKEN: " + sampleInputs.getToken());
		User user = userService.findUserByResetToken(sampleInputs.getToken());

		String passwordValidation = sampleInputs.getPassword();
		
		if (user == null) {
			modelAndView.addObject("error", "Oops! Ovo nije odogovarajući link za izmjenu lozinke.");
			modelAndView.setViewName("user/resetPassword");	
			} else if(!passwordValidation.matches("[a-zA-Z0-9]*") || passwordValidation.length() < 8 || passwordValidation.length() > 14) {
				modelAndView.addObject("error", "Oops! Lozinka mora da sadrži slova i brojeve, i ne smije biti kraća od 8 karaktera i duža od 14 karaktera.");
				modelAndView.setViewName("user/resetPassword");	
			} else if(passwordValidation.compareTo(sampleInputs.getConfirmPassword()) != 0){
				modelAndView.addObject("error", "Oops! Lozinka i potvrda lozinke nisu iste.");
				modelAndView.setViewName("user/resetPassword");	
			} else {
			
			User resetUser = user; 
            
			// Set new password   
			System.out.println("NEW PASSWORD: " + sampleInputs.getPassword());
            resetUser.setPassword(bCryptPasswordEncoder.encode(sampleInputs.getPassword()));
            
			// Set the reset token to null so it cannot be used again
			resetUser.setResetToken(null);

			// Save user with new password
			userService.resetUpdate(resetUser);

			modelAndView.addObject("message", "Uspješno ste izmjenili vašu lozinku. Sad se možete prijaviti na YAP web aplikaciju.");

			modelAndView.setViewName("user/login");
			return modelAndView;
		}
		return modelAndView;
   }
   
    // Going to reset page without a token redirects to login page
	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ModelAndView handleMissingParams(MissingServletRequestParameterException ex) {
		return new ModelAndView("redirect:login");
	}
}