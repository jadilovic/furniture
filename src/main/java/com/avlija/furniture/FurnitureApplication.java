package com.avlija.furniture;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.avlija.furniture.service.DefaultHelloService;
import com.avlija.furniture.service.HelloService;

@SpringBootApplication 
public class FurnitureApplication implements CommandLineRunner{

	public static void main(String[] args) {
		SpringApplication.run(FurnitureApplication.class, args);
	}
	
	@Bean
	public HelloService getHelloService(){
		return  new DefaultHelloService();
	}

	@Override
	public void run(String... args) throws Exception {
		getHelloService().hello();
	}

}
