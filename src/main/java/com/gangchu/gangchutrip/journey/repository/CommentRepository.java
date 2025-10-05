package com.gangchu.gangchutrip.journey.repository;

import com.gangchu.gangchutrip.journey.entity.Comment;
import com.gangchu.gangchutrip.journey.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    
    List<Comment> findByPostOrderByCreatedAtAsc(Post post);
    
    long countByPost(Post post);
}

