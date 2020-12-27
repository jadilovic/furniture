package com.avlija.furniture;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration 
public class MailConfig {

    @Bean
    public JavaMailSender javaMailService() {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();

        final String username = "yap.webapp@gmail.com";//your Gmail username 
        final String password = "Bossama0";//your Gmail password
        String host = "smtp.gmail.com";
        javaMailSender.setHost(host);
        javaMailSender.setPort(587);

        javaMailSender.setJavaMailProperties(getMailProperties());
        
     // Get the Session object
        Session session = Session.getInstance(getMailProperties(), new Authenticator() {
       	 protected PasswordAuthentication getPasswordAuthentication() {
       		 return new PasswordAuthentication(username, password);
       	 	}
        });
		
		javaMailSender.setSession(session);

        return javaMailSender;
    }

    private Properties getMailProperties() {
        Properties properties = new Properties();
        properties.setProperty("mail.transport.protocol", "smtp");
        properties.setProperty("mail.smtp.auth", "true");
        properties.setProperty("mail.smtp.starttls.enable", "false");
        properties.setProperty("mail.debug", "false");
        String host = "smtp.gmail.com";

        Properties props = new Properties();
         props.put("mail.smtp.auth", "true");
         props.put("mail.smtp.starttls.enable", "true"); 
         props.put("mail.smtp.host", host);
         props.put("mail.smtp.port", "587");
        return props;
    }
}