package com.avlija.furniture.repository;

import com.avlija.furniture.model.Pipeline;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PipelineRepository extends JpaRepository<Pipeline, Integer> {
    Pipeline findByName(String name);

	List<Pipeline> findByNameContaining(String keyWord);

}