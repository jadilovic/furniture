package com.avlija.furniture.model;

import java.io.Serializable;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.*;

	/**
	 * ELEMENT CLASS WITH INFORMATION ABOUT ELEMENTS
	 */
@Entity
@Table(name = "elements")
public class Element implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    
	@Column(name = "sifra")
    private String sifra;
	
	@Column(name = "name")
    private String name;
	
	@Column(name = "element_size")
    private double elementSize;
	
    @ManyToOne
    @JoinColumn(name ="FK_UnitMeasureId")
    private UnitMeasure unitMeasure;

	@Column(name = "quantity")
    private int quantity;
	
    @ManyToMany(mappedBy = "elements", fetch = FetchType.LAZY)
    private Set<Product> products = new TreeSet<>();

    public Element() {
    	
    }
	/**
	 * @return the products
	 */
	public Set<Product> getProducts() {
		return products;
	}

	/**
	 * @param products the products to set
	 */
	public void setProducts(Set<Product> products) {
		this.products = products;
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
	 * @return the sifra
	 */
	public String getSifra() {
		return sifra;
	}

	/**
	 * @param sifra the sifra to set
	 */
	public void setSifra(String sifra) {
		this.sifra = sifra;
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
	 * @return the elementSize
	 */
	public double getElementSize() {
		return elementSize;
	}

	/**
	 * @param elementSize the elementSize to set
	 */
	public void setElementSize(double elementSize) {
		this.elementSize = elementSize;
	}

	/**
	 * @return the unitMeasure
	 */
	public UnitMeasure getUnitMeasure() {
		return unitMeasure;
	}

	/**
	 * @param unitMeasure the unitMeasure to set
	 */
	public void setUnitMeasure(UnitMeasure unitMeasure) {
		this.unitMeasure = unitMeasure;
	}

	/**
	 * @return the quantity
	 */
	public int getQuantity() {
		return quantity;
	}

	/**
	 * @param quantity the quantity to set
	 */
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	
}