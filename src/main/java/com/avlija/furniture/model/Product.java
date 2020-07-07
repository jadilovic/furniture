package com.avlija.furniture.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

import javax.persistence.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "products")
public class Product {
   
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "product_id")
    private int id;
	
	@Column(name = "name")
    private String name;

	 @ManyToMany(cascade=CascadeType.ALL)
	 @JoinTable(name="product_element",
	 joinColumns=@JoinColumn(name="product_id"),
	 inverseJoinColumns=@JoinColumn(name="element_id"))
	 private Set<Element> elements;

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