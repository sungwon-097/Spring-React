package com.example.server.repository;

import com.example.server.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> { // user 의 기본키는 int 형이라는 뜻
    public User findByUsername(String username);
    public User findByEmail(String email);
}
