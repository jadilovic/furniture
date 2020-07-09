package com.avlija.furniture.model;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.avlija.furniture.repository.ElementRepository;



@Component
public class ElementConverter implements Converter<String, Element>{
	
    @Autowired
    private ElementRepository elementRepository;

	@Override
	public Element convert(String id) {

		int parsedId = Integer.parseInt(id);
		Element element = elementRepository.findById(parsedId).get();
		return element;
	}

}
