
package com.avlija.furniture.model;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

@Embeddable
public class ListProduct implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@NotNull
	private int listId;
	
	@NotNull
	private int productId;
	
	public ListProduct() {
	}

	public ListProduct(@NotNull int listId, @NotNull int productId) {
		this.listId = listId;
		this.productId = productId;
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
	 * @return the listId
	 */
	public int getListId() {
		return listId;
	}

	/**
	 * @param listId the listId to set
	 */
	public void setListId(int listId) {
		this.listId = listId;
	}


}
