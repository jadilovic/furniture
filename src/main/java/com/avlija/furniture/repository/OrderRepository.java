package com.avlija.furniture.repository;

import com.avlija.furniture.model.Order;
import com.avlija.furniture.model.Product;

import java.sql.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
	
	List<Order> findByProduct(Product product);

	List<Order> findByCreated(Date date);

}