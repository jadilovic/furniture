package com.avlija.furniture.repository;

/**
 * ORDER REPOSITORY CONNECTED TO THE MYSQL DATABASE TABLE 
 * TABLE ORDERS CONTAINS INFORMATION ABOUT EACH ORDER IN THE DATABASE
 */
import com.avlija.furniture.model.Order;
import com.avlija.furniture.model.Pipeline;

import java.sql.Date;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
	
	List<Order> findByPipeline(Pipeline pipeline, Sort sort);

	@Query("SELECT n FROM Order n WHERE date(n.created) = ?1")
	List<Order> findByCreatedDate(Date date);

	@Query("SELECT n FROM Order n ORDER BY n.created DESC")
	List<Order> findAllByCreatedDesc();
	
	Order findByPipeline(Pipeline pipeline);
	
	 List<Order> findFirst10ByOrderCompletedOrderByIdDesc(int completed);

}