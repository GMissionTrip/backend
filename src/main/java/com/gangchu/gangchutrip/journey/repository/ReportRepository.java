package com.gangchu.gangchutrip.journey.repository;

import com.gangchu.gangchutrip.auth.entity.Member;
import com.gangchu.gangchutrip.journey.entity.Post;
import com.gangchu.gangchutrip.journey.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    
    boolean existsByPostAndMember(Post post, Member member);
    
    long countByPost(Post post);
}

