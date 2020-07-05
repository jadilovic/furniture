package com.avlija.furniture.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "elements")
public class Element {
   
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "element_id")
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