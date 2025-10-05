package com.gangchu.gangchutrip.journey.repository;

import com.gangchu.gangchutrip.auth.entity.Member;
import com.gangchu.gangchutrip.journey.entity.Post;
import com.gangchu.gangchutrip.journey.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    
    Optional<PostLike> findByPostAndMember(Post post, Member member);
    
    boolean existsByPostAndMember(Post post, Member member);
    
    void deleteByPostAndMember(Post post, Member member);
}

