
package com.avlija.furniture.repository;

import org.springframework.stereotype.Repository;

import com.avlija.furniture.model.ElementQuantity;
import com.avlija.furniture.model.ProductElement;

import org.springframework.data.jpa.repository.JpaRepository;


@Repository
public interface ElementQuantityRepository extends JpaRepository<ElementQuantity, ProductElement>{

}

