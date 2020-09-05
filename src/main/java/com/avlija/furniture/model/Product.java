package com.avlija.furniture.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.*;

@Entity
@Table(name = "products")
public class Product implements Serializable{
   
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
	
	@Column(name = "name")
    private String name;
	
	@Column(name = "product_size")
    private String productSize;
	
    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinTable(
            name = "products_elements",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "element_id")
            )
    private Set <Element> elements = new TreeSet<>();
    
    @ManyToMany(mappedBy = "products", fetch = FetchType.LAZY)
    private Set<Pipeline> pipelines = new TreeSet<>();
    
    public Product() {
    	
    }

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
	public Set <Element> getElements() {
		return elements;
	}

	/**
	 * @param elements the elements to set
	 */
	public void setElements(Set <Element> elements) {
		this.elements = elements;
	}

	/**
	 * @return the productSize
	 */
	public String getProductSize() {
		return productSize;
	}

	/**
	 * @param productSize the productSize to set
	 */
	public void setProductSize(String productSize) {
		this.productSize = productSize;
	}

	/**
	 * @return the pipelines
	 */
	public Set<Pipeline> getPipelines() {
		return pipelines;
	}

	/**
	 * @param pipelines the pipelines to set
	 */
	public void setPipelines(Set<Pipeline> pipelines) {
		this.pipelines = pipelines;
	}


}