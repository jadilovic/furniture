
package com.avlija.furniture.repository;

import org.springframework.stereotype.Repository;

import com.avlija.furniture.model.ProductQuantity;
import com.avlija.furniture.model.PipelineProduct;

import org.springframework.data.jpa.repository.JpaRepository;


@Repository
public interface ProductQuantityRepository extends JpaRepository<ProductQuantity, PipelineProduct>{

}
