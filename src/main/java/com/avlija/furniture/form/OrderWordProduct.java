package com.avlija.furniture.form;

public class OrderWordProduct {

	private String rankNum;
	private String productName;
	private String productDim;
	private String quantity;
	private String note;
	private String buyer;
	
	public OrderWordProduct() {
	}

	public OrderWordProduct(String rankNum, String productName, String productDim, String quantity, String note,
			String buyer) {
		super();
		this.rankNum = rankNum;
		this.productName = productName;
		this.productDim = productDim;
		this.quantity = quantity;
		this.note = note;
		this.buyer = buyer;
	}

	/**
	 * @return the orderNum
	 */
	public String getRankNum() {
		return rankNum;
	}

	/**
	 * @param orderNum the orderNum to set
	 */
	public void setRankNum(String orderNum) {
		this.rankNum = orderNum;
	}

	/**
	 * @return the productName
	 */
	public String getProductName() {
		return productName;
	}

	/**
	 * @param productName the productName to set
	 */
	public void setProductName(String productName) {
		this.productName = productName;
	}

	/**
	 * @return the productDim
	 */
	public String getProductDim() {
		return productDim;
	}

	/**
	 * @param productDim the productDim to set
	 */
	public void setProductDim(String productDim) {
		this.productDim = productDim;
	}

	/**
	 * @return the quantity
	 */
	public String getQuantity() {
		return quantity;
	}

	/**
	 * @param quantity the quantity to set
	 */
	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	/**
	 * @return the note
	 */
	public String getNote() {
		return note;
	}

	/**
	 * @param note the note to set
	 */
	public void setNote(String note) {
		this.note = note;
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
