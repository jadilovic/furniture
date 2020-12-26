package com.avlija.furniture.repository;

import com.avlija.furniture.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    User findByEmail(String email);
    User findByUserName(String userName);
    User findByResetToken(String resetToken);
}