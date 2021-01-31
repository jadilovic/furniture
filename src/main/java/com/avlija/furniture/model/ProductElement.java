
package com.avlija.furniture.model;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

/**
 * PRODUCT-ELEMENT CLASS WHICH SERVES AS A PRIMARY KEY FOR INDIVIDUAL PRODUCTS AND THEIR ELEMENTS
 */

@Embeddable
public class ProductElement implements Serializable{

	private static final long serialVersionUID = 1L;

	@NotNull
	private int productId;
	
	@NotNull
	private int elementId;
	
	public ProductElement() {
	}

	public ProductElement(@NotNull int productId, @NotNull int elementId) {
		this.productId = productId;
		this.elementId = elementId;
	}

	/**
	 * @return the productId
	 */
	public int getProductId() {
		return productId;
	}

	/**
	 * @param productId the productId to set
	 */
	public void setProductId(int productId) {
		this.productId = productId;
	}

	/**
	 * @return the elementId
	 */
	public int getElementId() {
		return elementId;
	}

	/**
	 * @param elementId the elementId to set
	 */
	public void setElementId(int elementId) {
		this.elementId = elementId;
	}

}
