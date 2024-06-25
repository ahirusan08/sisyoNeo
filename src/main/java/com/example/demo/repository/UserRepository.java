package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

	public abstract Optional<User> findByEmailAndPassword(String email, String password);
	
	public abstract Optional<User> findByEmail(String email);

}
