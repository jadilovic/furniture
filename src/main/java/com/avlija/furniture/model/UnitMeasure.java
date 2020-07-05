package com.avlija.furniture.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "unit_measures")
public class UnitMeasure {
   
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "element_id")
    private int id;
	
	@Column(name = "name")
    private String name;
	
    
    @OneToMany(mappedBy="unitMeasure", cascade = CascadeType.ALL)
    Set<Element> products = new HashSet<Element>();


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
	 * @return the products
	 */
	public Set<Element> getProducts() {
		return products;
	}


	/**
	 * @param products the products to set
	 */
	public void setProducts(Set<Element> products) {
		this.products = products;
	}


}