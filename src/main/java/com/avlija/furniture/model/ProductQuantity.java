package com.avlija.furniture.model;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "product_quantity")
public class ProductQuantity {

	@EmbeddedId
	private ListProduct listProduct;
	
	@NotNull
	private int quantity;
	
	public ProductQuantity() {
	}

	public ProductQuantity(ListProduct listProduct, @NotNull int quantity) {
		this.listProduct = listProduct;
		this.quantity = quantity;
	}

	/**
	 * @return the listProduct
	 */
	public ListProduct getListProduct() {
		return listProduct;
	}

	/**
	 * @param listProduct the listProduct to set
	 */
	public void setListProduct(ListProduct listProduct) {
		this.listProduct = listProduct;
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

