package com.example.demo;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Relationship {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    Integer friend1id;
    Integer friend2id;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getFriend1id() {
        return friend1id;
    }

    public void setFriend1id(Integer friend1id) {
        this.friend1id = friend1id;
    }

    public Integer getFriend2id() {
        return friend2id;
    }

    public void setFriend2id(Integer friend2id) {
        this.friend2id = friend2id;
    }
}
