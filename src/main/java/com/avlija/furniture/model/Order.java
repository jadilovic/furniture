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
	
	@Column(name = "date_created")
    private Date created;
    
	@Column(name = "work_position")
    private String workPosition;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pipeline_id", nullable = false)
    private Pipeline pipeline;
  
    @Column(name = "order_comment")
    private String orderComment;
    
    @Column(name = "order_packaging")
    private String orderPackaging;
    
    @Column(name = "order_prep")
    private String orderPrep;
    
    @Column(name = "order_completed")
    private int orderCompleted;
    
    public Order() {
    }

	public Order(Date created, String workPosition, Pipeline pipeline, String orderComment, String orderPackaging,
			String orderPrep, int orderCompleted) {
		this.created = created;
		this.workPosition = workPosition;
		this.pipeline = pipeline;
		this.orderComment = orderComment;
		this.orderPackaging = orderPackaging;
		this.orderPrep = orderPrep;
		this.orderCompleted = orderCompleted;
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
	 * @return the pipeline
	 */
	public Pipeline getPipeline() {
		return pipeline;
	}

	/**
	 * @param pipeline the pipeline to set
	 */
	public void setPipeline(Pipeline pipeline) {
		this.pipeline = pipeline;
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
	 * @return the orderCompleted
	 */
	public int getOrderCompleted() {
		return orderCompleted;
	}

	/**
	 * @param orderCompleted the orderCompleted to set
	 */
	public void setOrderCompleted(int orderCompleted) {
		this.orderCompleted = orderCompleted;
	}
    
	
}