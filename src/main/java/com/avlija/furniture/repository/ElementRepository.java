package com.avlija.furniture.repository;

import com.avlija.furniture.model.Element;

/**
 * ELEMENT REPOSITORY CONNECTED TO THE MYSQL DATABASE TABLE 
 * TABLE ELEMENTS CONTAINS INFORMATION ABOUT EACH ELEMENT IN THE DATABASE
 */

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ElementRepository extends JpaRepository<Element, Integer> {
    Element findBySifra(String sifra);
    
    Page<Element> findBySifra(String sifra, Pageable pageable);

	List<Element> findByNameContaining(String keyWord);
	
	Page<Element> findAll(Pageable pageable);
	
	Page<Element> findByNameContaining(String keyWord, Pageable pageable);


}