package com.avlija.furniture.form;

import org.springframework.format.annotation.DateTimeFormat;

import com.avlija.furniture.model.Element;
import com.avlija.furniture.model.Product;

import java.util.Date;
import java.util.List;

public class SampleInputs {

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dateField;
    private Integer id;
    private int pipelineId;
    private int userId;
    private int postId;
    private Double elementSize;
    private int elmId;
    private int prdId;
    private Integer quantity;
    private String sifra;
    private String name;
    private String searchDate;
    private String modelName;
    private String groupName;
    private String email;
    private String password;
    private String token;
    private String confirmPassword;
    private String keyWord;
    private String prdComment;
    private String prdBuyer;
    private String workPosition;
    private String orderComment;
    private String orderPackaging;
    private String orderPrep;
    private Long orderId;
    
    private List <Element> selectedElements;
    private List<Product> selectedProducts;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private Date dateTimeField;

    public SampleInputs() {
    	
    }

	/**
	 * @return the elmId
	 */
	public int getElmId() {
		return elmId;
	}

	/**
	 * @param elmId the elmId to set
	 */
	public void setElmId(int elmId) {
		this.elmId = elmId;
	}

	/**
	 * @return the prdId
	 */
	public int getPrdId() {
		return prdId;
	}

	/**
	 * @param prdId the prdId to set
	 */
	public void setPrdId(int prdId) {
		this.prdId = prdId;
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
	 * @return the dateField
	 */
	public Date getDateField() {
		return dateField;
	}

	/**
	 * @param dateField the dateField to set
	 */
	public void setDateField(Date dateField) {
		this.dateField = dateField;
	}

	/**
	 * @return the quantity
	 */
	public Integer getQuantity() {
		return quantity;
	}

	/**
	 * @param quantity the quantity to set
	 */
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	/**
	 * @return the dateTimeField
	 */
	public Date getDateTimeField() {
		return dateTimeField;
	}

	/**
	 * @param dateTimeField the dateTimeField to set
	 */
	public void setDateTimeField(Date dateTimeField) {
		this.dateTimeField = dateTimeField;
	}

	/**
	 * @return the modelName
	 */
	public String getModelName() {
		return modelName;
	}

	/**
	 * @param modelName the modelName to set
	 */
	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	/**
	 * @return the groupName
	 */
	public String getGroupName() {
		return groupName;
	}

	/**
	 * @param groupName the groupName to set
	 */
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the token
	 */
	public String getToken() {
		return token;
	}

	/**
	 * @param token the token to set
	 */
	public void setToken(String token) {
		this.token = token;
	}

	/**
	 * @return the confirmPassword
	 */
	public String getConfirmPassword() {
		return confirmPassword;
	}

	/**
	 * @param confirmPassword the confirmPassword to set
	 */
	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

	public int getPostId() {
		return postId;
	}

	public void setPostId(int postId) {
		this.postId = postId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public Double getElementSize() {
		return elementSize;
	}

	public void setElementSize(Double elementSize) {
		this.elementSize = elementSize;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List <Element> getSelectedElements() {
		return selectedElements;
	}

	public void setSelectedElements (List <Element> selectedElements) {
		this.selectedElements = selectedElements;
	}

	public String getKeyWord() {
		return keyWord;
	}

	public void setKeyWord(String keyWord) {
		this.keyWord = keyWord;
	}

	public String getSearchDate() {
		return searchDate;
	}

	public void setSearchDate(String searchDate) {
		this.searchDate = searchDate;
	}


	public List<Product> getSelectedProducts() {
		return selectedProducts;
	}


	public void setSelectedProducts(List<Product> selectedProducts) {
		this.selectedProducts = selectedProducts;
	}


	public int getPipelineId() {
		return pipelineId;
	}


	public void setPipelineId(int pipelineId) {
		this.pipelineId = pipelineId;
	}

	/**
	 * @return the prdComment
	 */
	public String getPrdComment() {
		return prdComment;
	}

	/**
	 * @param prdComment the prdComment to set
	 */
	public void setPrdComment(String prdComment) {
		this.prdComment = prdComment;
	}

	/**
	 * @return the prdBuyer
	 */
	public String getPrdBuyer() {
		return prdBuyer;
	}

	/**
	 * @param prdBuyer the prdBuyer to set
	 */
	public void setPrdBuyer(String prdBuyer) {
		this.prdBuyer = prdBuyer;
	}

	/**
	 * @return the workPosition
	 */
	public String getWorkPosition() {
		return workPosition;
	}

	/**
	 * @param workPosition the workPosition to set
	 */
	public void setWorkPosition(String workPosition) {
		this.workPosition = workPosition;
	}

	/**
	 * @return the orderComment
	 */
	public String getOrderComment() {
		return orderComment;
	}

	/**
	 * @param orderComment the orderComment to set
	 */
	public void setOrderComment(String orderComment) {
		this.orderComment = orderComment;
	}

	/**
	 * @return the orderPackaging
	 */
	public String getOrderPackaging() {
		return orderPackaging;
	}

	/**
	 * @param orderPackaging the orderPackaging to set
	 */
	public void setOrderPackaging(String orderPackaging) {
		this.orderPackaging = orderPackaging;
	}

	/**
	 * @return the orderPrep
	 */
	public String getOrderPrep() {
		return orderPrep;
	}

	/**
	 * @param orderPrep the orderPrep to set
	 */
	public void setOrderPrep(String orderPrep) {
		this.orderPrep = orderPrep;
	}

	/**
	 * @return the orderId
	 */
	public Long getOrderId() {
		return orderId;
	}

	/**
	 * @param orderId the orderId to set
	 */
	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}
	
	
}