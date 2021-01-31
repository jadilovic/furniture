package com.avlija.furniture.model;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * PRODUCT-QUANTITY CLASS WHICH CONTAINS INFORMATION ABOUT QUANTITY OF CERTAIN PRODUCT IN THE PIPELINE
 */

@Entity
@Table(name = "product_quantity")
public class ProductQuantity {

	@EmbeddedId
	private PipelineProduct pipelineProduct;
	
	@NotNull
	private int quantity;
	
	 @Column(name="comments")
	 private String comment;
	 
	 @Column(name="buyers")
	 private String buyer;
	
	public ProductQuantity() {
	}

	public ProductQuantity(PipelineProduct pipelineProduct, @NotNull int quantity, String comment, String buyer) {
		this.pipelineProduct = pipelineProduct;
		this.quantity = quantity;
		this.comment = comment;
		this.buyer = buyer;
	}

	/**
	 * @return the pipelineProduct
	 */
	public PipelineProduct getPipelineProduct() {
		return pipelineProduct;
	}

	/**
	 * @param pipelineProduct the pipelineProduct to set
	 */
	public void setPipelineProduct(PipelineProduct pipelineProduct) {
		this.pipelineProduct = pipelineProduct;
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

