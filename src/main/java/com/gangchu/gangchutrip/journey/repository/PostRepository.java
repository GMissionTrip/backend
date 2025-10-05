package com.gangchu.gangchutrip.journey.repository;

import com.gangchu.gangchutrip.journey.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    
    // 활성 상태인 게시물만 조회
    List<Post> findByStatusOrderByCreatedAtDesc(Post.PostStatus status);
    
    // 인기 게시물 조회 (좋아요 + 댓글 + 조회수 기반)
    @Query("SELECT p FROM Post p WHERE p.status = 'ACTIVE' " +
           "ORDER BY (p.likes * 2 + p.comments * 3 + p.views * 0.1) DESC")
    List<Post> findPopularPosts();
    
    // 활성 상태인 특정 게시물 조회
    Optional<Post> findByIdAndStatus(Long id, Post.PostStatus status);
}

