package com.avlija.furniture.repository;

import com.avlija.furniture.model.UnitMeasure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
/**
 * UNIT MEASURE REPOSITORY CONNECTED TO THE MYSQL DATABASE TABLE 
 * TABLE UNIT_MEASURES CONTAINS INFORMATION ABOUT EACH UNIT MEASURE IN THE DATABASE
 */
@Repository
public interface UnitMeasureRepository extends JpaRepository<UnitMeasure, Integer> {
    UnitMeasure findByUnitMeasureName(String name);

}