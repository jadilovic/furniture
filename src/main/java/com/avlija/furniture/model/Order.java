package com.avlija.furniture.model;

import javax.persistence.*;

import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name="orders")
public class Order implements Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
	
	@Temporal(TemporalType.DATE)
    private Date created;
    
    private int orderQuant;

  // private double totalValue;
    
  //  private String transType;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id")
    private Product product;
  
    /*
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    */
    
    public Order() {
    }

	public Order(Date created, int orderQuant, Product product) {
		this.created = created;
		this.orderQuant = orderQuant;
		this.product = product;
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the created
	 */
	public Date getCreated() {
		return created;
	}

	/**
	 * @param created the created to set
	 */
	public void setCreated(Date created) {
		this.created = created;
	}

	/**
	 * @return the orderQuant
	 */
	public int getOrderQuant() {
		return orderQuant;
	}

	/**
	 * @param orderQuant the orderQuant to set
	 */
	public void setOrderQuant(int orderQuant) {
		this.orderQuant = orderQuant;
	}

	/**
	 * @return the product
	 */
	public Product getProduct() {
		return product;
	}

	/**
	 * @param product the product to set
	 */
	public void setProduct(Product product) {
		this.product = product;
	}
    
}