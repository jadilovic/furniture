package com.avlija.furniture.repository;

import com.avlija.furniture.model.Product;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
/**
 * PRODUCT REPOSITORY CONNECTED TO THE MYSQL DATABASE TABLE 
 * TABLE PRODUCTS CONTAINS INFORMATION ABOUT EACH PRODUCT IN THE DATABASE
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    
	Product findByName(String name);

	List<Product> findByNameContaining(String keyWord);

	Page<Product> findAll(Pageable pageable);
}