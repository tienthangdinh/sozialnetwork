package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {
    @Autowired
    Postrepository repo;

    public void deletepostbyid(Integer id) {repo.deleteById(id);}

    public void save(Post post) {
        repo.save(post);
    }

    public List<Post> loadpostsbyusername(String username) {
        return repo.findAllByUsername(username);
    }
}
