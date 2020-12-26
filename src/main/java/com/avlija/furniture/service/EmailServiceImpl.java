package com.avlija.furniture.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/*
 * Used in PasswordController and MailConfig
 */

@Service("emailService")
public class EmailServiceImpl implements EmailService {

	@Autowired
	private JavaMailSender mailSender;

	// PasswordController line 48
	@Async
	public void sendEmail(SimpleMailMessage email) {
		mailSender.send(email);
	}
}