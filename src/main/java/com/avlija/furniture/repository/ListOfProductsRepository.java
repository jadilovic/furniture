package com.avlija.furniture.repository;

import com.avlija.furniture.model.ListOfProducts;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ListOfProductsRepository extends JpaRepository<ListOfProducts, Integer> {
    ListOfProducts findByName(String name);

	List<ListOfProducts> findByNameContaining(String keyWord);

}