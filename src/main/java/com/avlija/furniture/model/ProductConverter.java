package com.avlija.furniture.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.avlija.furniture.repository.ProductRepository;



@Component
public class ProductConverter implements Converter<String, Product>{
	
    @Autowired
    private ProductRepository productRepository;

	@Override
	public Product convert(String id) {

		int parsedId = Integer.parseInt(id);
		Product product = productRepository.findById(parsedId).get();
		return product;
	}

}
