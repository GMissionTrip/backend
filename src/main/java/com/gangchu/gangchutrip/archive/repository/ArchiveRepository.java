package com.gangchu.gangchutrip.archive.repository;

import com.gangchu.gangchutrip.global.entity.Archive;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ArchiveRepository extends JpaRepository<Archive, Long> {

    List<Archive> findAllByMemberId(Long memberId);

    @Query("SELECT a FROM Archive a WHERE a.memberId = :memberId")
    List<Archive> findByMemberId(@Param("memberId") Long memberId);

    @Query("SELECT a FROM Archive a WHERE a.travelId = :travelId")
    List<Archive> findByTravelId(@Param("travelId") Long travelId);

    boolean existsByMemberIdAndTravelId(Long memberId, Long travelId);
}
