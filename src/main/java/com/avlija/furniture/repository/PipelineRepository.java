package com.avlija.furniture.repository;
/**
 * PIPELINE REPOSITORY CONNECTED TO THE MYSQL DATABASE TABLE 
 * TABLE PIPELINES CONTAINS INFORMATION ABOUT EACH PIPELINE (TEMPLATE WITH LIST OF PRODUCTS FOR ORDERS)
 */
import com.avlija.furniture.model.Pipeline;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PipelineRepository extends JpaRepository<Pipeline, Integer> {
    Pipeline findByName(String name);
    
    Page<Pipeline> findAll(Pageable pageable);

	List<Pipeline> findByNameContaining(String keyWord);
	
	List<Pipeline> findFirst10ByActive(int active, Sort sort);

}