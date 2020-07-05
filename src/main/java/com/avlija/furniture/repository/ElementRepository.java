package com.avlija.furniture.repository;

import com.avlija.furniture.model.Element;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ElementRepository extends JpaRepository<Element, Integer> {
    Element findBySifra(String sifra);

}