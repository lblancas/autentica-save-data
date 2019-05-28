package com.example.demo.repository;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.domain.User;
public interface UserRepository extends JpaRepository<User, Long> 
{
    Optional<User> findByUsername(String username);
    List<User> findByUsernameAndActivo(String username,Integer activo);
}
