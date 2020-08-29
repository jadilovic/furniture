package com.avlija.furniture.model;

import javax.persistence.Column;
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
	
	 @Column(name="comments")
	 private String comment;
	 
	 @Column(name="buyers")
	 private String buyer;
	
	public ProductQuantity() {
	}

	public ProductQuantity(ListProduct listProduct, @NotNull int quantity, String comment, String buyer) {
		this.listProduct = listProduct;
		this.quantity = quantity;
		this.comment = comment;
		this.buyer = buyer;
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

	/**
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @param comment the comment to set
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * @return the buyer
	 */
	public String getBuyer() {
		return buyer;
	}

	/**
	 * @param buyer the buyer to set
	 */
	public void setBuyer(String buyer) {
		this.buyer = buyer;
	}
	
	
}

