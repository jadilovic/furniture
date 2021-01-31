package com.avlija.furniture.repository;

import com.avlija.furniture.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
/**
 * USER REPOSITORY CONNECTED TO THE MYSQL DATABASE TABLE 
 * TABLE USERS CONTAINS INFORMATION ABOUT EACH USER
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    User findByEmail(String email);
    User findByUserName(String userName);
    User findByResetToken(String resetToken);
}