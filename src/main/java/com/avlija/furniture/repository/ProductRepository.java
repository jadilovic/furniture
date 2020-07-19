package com.avlija.furniture.repository;

import com.avlija.furniture.model.Product;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    Product findByName(String name);

	List<Product> findByNameContaining(String keyWord);

}