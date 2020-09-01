package com.avlija.furniture.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;


@Entity
@Table(name = "unit_measures")
public class UnitMeasure {
   
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private int id;
	
	@Column(name = "name")
    private String name;
	
    
    @OneToMany(mappedBy="unitMeasure", cascade = CascadeType.ALL)
    Set<Element> elements = new HashSet<Element>();


	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}


	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}


	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}


	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}


	/**
	 * @return the elements
	 */
	public Set<Element> getElements() {
		return elements;
	}


	/**
	 * @param elements the elements to set
	 */
	public void setElements(Set<Element> elements) {
		this.elements = elements;
	}



}