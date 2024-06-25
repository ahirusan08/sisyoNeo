package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Host;

@Repository
public interface HostRepository extends JpaRepository<Host, Integer> {

	public abstract Optional<Host> findByIdAndPassword(Integer id, String password);
}
