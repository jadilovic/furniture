package com.avlija.furniture.repository;
/**
 * ROLE REPOSITORY CONNECTED TO THE MYSQL DATABASE TABLE 
 * TABLE ROLES CONTAINS INFORMATION ABOUT EACH ROLE IN THE DATABASE
 */
import com.avlija.furniture.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("roleRepository")
public interface RoleRepository extends JpaRepository<Role, Integer> {
    Role findByRole(String role);

}