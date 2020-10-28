package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface Postrepository extends JpaRepository<Post, Integer> {
    List<Post> findAllByUsername(String username);
}
