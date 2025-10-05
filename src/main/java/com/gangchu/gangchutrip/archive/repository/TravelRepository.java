package com.gangchu.gangchutrip.archive.repository;

import com.gangchu.gangchutrip.global.entity.Travel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TravelRepository extends JpaRepository<Travel, Long> {
    List<Travel> findByMemberIdOrderByCreatedDateDesc(Long memberId);
    List<Travel> findByMemberIdAndStatus(Long memberId, Travel.TravelStatus status);
}
