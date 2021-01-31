package com.avlija.furniture.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

/**
 * UNIT MEASURE CLASS CONTAINS INFORMATION ABOUT DIFFERENT UNIT MEASURES
 */

@Entity
@Table(name = "unit_measures")
public class UnitMeasure {
   
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "unit_measure_id")
    private int unitMeasureId;
	
	@Column(name = "unit_measure_name")
    private String unitMeasureName;
	
    
    @OneToMany(mappedBy="unitMeasure", cascade = CascadeType.ALL)
    Set<Element> elements = new HashSet<Element>();


	/**
	 * @return the unitMeasureId
	 */
	public int getUnitMeasureId() {
		return unitMeasureId;
	}


	/**
	 * @param unitMeasureId the unitMeasureId to set
	 */
	public void setUnitMeasureId(int unitMeasureId) {
		this.unitMeasureId = unitMeasureId;
	}


	/**
	 * @return the unitMeasureName
	 */
	public String getUnitMeasureName() {
		return unitMeasureName;
	}


	/**
	 * @param unitMeasureName the unitMeasureName to set
	 */
	public void setUnitMeasureName(String unitMeasureName) {
		this.unitMeasureName = unitMeasureName;
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