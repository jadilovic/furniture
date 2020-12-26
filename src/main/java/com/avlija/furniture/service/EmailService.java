package com.avlija.furniture.service;

import org.springframework.mail.SimpleMailMessage;
/*
 * Used in PasswordController and MailConfig
 */

// PasswordController line 48
public interface EmailService {
	public void sendEmail(SimpleMailMessage email);
}