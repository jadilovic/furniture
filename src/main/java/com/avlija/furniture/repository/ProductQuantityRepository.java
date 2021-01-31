
package com.avlija.furniture.repository;

import org.springframework.stereotype.Repository;

import com.avlija.furniture.model.ProductQuantity;
import com.avlija.furniture.model.PipelineProduct;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * PRODUCT-QUANTITY REPOSITORY CONNECTED TO THE MYSQL DATABASE TABLE 
 * TABLE PRODUCT_QUANTITY CONTAINS INFORMATION ABOUT QUANTITY OF EACH PRODUCT IN THE PIPELINE
 */
@Repository
public interface ProductQuantityRepository extends JpaRepository<ProductQuantity, PipelineProduct>{

}

