package com.avlija.furniture.repository;

import com.avlija.furniture.model.UnitMeasure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UnitMeasureRepository extends JpaRepository<UnitMeasure, Integer> {
    UnitMeasure findByUnitMeasureName(String name);

}