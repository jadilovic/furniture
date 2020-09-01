
package com.avlija.furniture.model;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

@Embeddable
public class PipelineProduct implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@NotNull
	private int pipelineId;
	
	@NotNull
	private int productId;
	
	public PipelineProduct() {
	}

	public PipelineProduct(@NotNull int pipelineId, @NotNull int productId) {
		this.pipelineId = pipelineId;
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
	 * @return the pipelineId
	 */
	public int getPipelineId() {
		return pipelineId;
	}

	/**
	 * @param pipelineId the pipelineId to set
	 */
	public void setPipelineId(int pipelineId) {
		this.pipelineId = pipelineId;
	}


}
