
package com.avlija.furniture.repository;

import org.springframework.stereotype.Repository;

import com.avlija.furniture.model.ElementQuantity;
import com.avlija.furniture.model.ProductElement;

import org.springframework.data.jpa.repository.JpaRepository;


/**
 * ELEMENT QUANTITY REPOSITORY CONNECTED TO THE MYSQL DATABASE TABLE 
 * TABLE ELEMENT_QUANTITY CONTAINS INFORMATION ABOUT QUANTITY OF EACH ELEMENT IN THE PRODUCT
 */

@Repository
public interface ElementQuantityRepository extends JpaRepository<ElementQuantity, ProductElement>{

}

