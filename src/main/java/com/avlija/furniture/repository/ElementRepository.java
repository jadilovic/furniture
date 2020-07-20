package com.avlija.furniture.repository;

import com.avlija.furniture.model.Element;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ElementRepository extends JpaRepository<Element, Integer> {
    Element findBySifra(String sifra);

	List<Element> findByNameContaining(String keyWord);

}