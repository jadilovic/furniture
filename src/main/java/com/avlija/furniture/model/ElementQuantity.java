package com.avlija.furniture.model;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "element_quantity")
public class ElementQuantity {

	@EmbeddedId
	private ProductElement productElement;
	
	@NotNull
	private int quantity;
	
	public ElementQuantity() {
	}

	public ElementQuantity(ProductElement productElement, @NotNull int quantity) {
		this.productElement = productElement;
		this.quantity = quantity;
	}

	/**
	 * @return the productElement
	 */
	public ProductElement getProductElement() {
		return productElement;
	}

	/**
	 * @param productElement the productElement to set
	 */
	public void setProductElement(ProductElement productElement) {
		this.productElement = productElement;
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

