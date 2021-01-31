package com.avlija.furniture.service;

import com.avlija.furniture.model.Role;
import com.avlija.furniture.model.User;
import com.avlija.furniture.repository.RoleRepository;
import com.avlija.furniture.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
/**
 * SAVING, UPDATING AND FINDING USERS
 */
@Service
public class UserService {

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       BCryptPasswordEncoder bCryptPasswordEncoder) {
        
    	this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User findUserByUserName(String userName) {
        return userRepository.findByUserName(userName);
    }
    
    public User findUserById(int id) {
        return userRepository.findById(id).get();
    }

    public User saveUser(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setActive(true);
        Role userRole = roleRepository.findByRole(user.getRole());
        user.setRoles(new HashSet<Role>(Arrays.asList(userRole)));
        return userRepository.save(user);
    }
    
    public void updateUser(User user) {
    	  Role userRole = roleRepository.findByRole(user.getRole());
    	  user.setRoles(new HashSet<Role>(Arrays.asList(userRole)));
    	  userRepository.save(user);
    }


	public List<User> findAllUsers() {
		return userRepository.findAll();
	}

	// PasswordController
	public void resetUpdate(User user) {
		  userRepository.save(user);
	}
	
	public
	User findUserByResetToken(String resetToken) {
		return userRepository.findByResetToken(resetToken);
	}
}