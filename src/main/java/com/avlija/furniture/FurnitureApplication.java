package com.avlija.furniture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.avlija.furniture.service.HelloWorldService;

@SpringBootApplication 
public class FurnitureApplication implements CommandLineRunner{
	
	  @Autowired
	  HelloWorldService helloWorldService;

	public static void main(String[] args) {
		SpringApplication.run(FurnitureApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		System.out.println(helloWorldService.getGreeting(args[0]));		
	}

}
