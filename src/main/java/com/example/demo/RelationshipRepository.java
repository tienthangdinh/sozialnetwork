package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

public interface RelationshipRepository extends JpaRepository<Relationship, Integer> {
    public List<Relationship> findAllByFriend1id(Integer friend1id);
    public List<Relationship> findAllByFriend2id(Integer friend2id);

    @Transactional
    @Modifying
    @Query(value = "delete from Relationship u where (u.friend1id = :id1 and u.friend2id = :id2) or (u.friend1id = :id2 and u.friend2id = :id1)")
    public void deleteRelationship(@Param("id1") Integer id1, @Param("id2") Integer id2);



}
