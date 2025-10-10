package com.gangchu.gangchutrip.mission.repository;

import com.gangchu.gangchutrip.global.entity.Mission;
import com.gangchu.gangchutrip.global.entity.MissionResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MissionResultRepository extends JpaRepository<MissionResult, Long> {

    boolean existsByMissionIdAndTouristIdAndTravelIdAndMemberId(
            Long missionId, Long touristId, String travelId, Long memberId);

    @Query("SELECT mr FROM MissionResult mr WHERE mr.touristId = :touristId")
    List<MissionResult> findByTouristId(@Param("touristId") Long touristId);

    @Query("SELECT mr FROM MissionResult mr WHERE mr.memberId = :memberId")
    List<MissionResult> findByMemberId(@Param("memberId") Long memberId);

    @Query("SELECT mr FROM MissionResult mr WHERE mr.travelId = :travelId")
    List<MissionResult> findByTravelId(@Param("travelId") String travelId);

    @Query("SELECT mr FROM MissionResult mr WHERE mr.missionId = :missionId")
    List<MissionResult> findByMissionId(@Param("missionId") Long missionId);

    @Query("SELECT mr FROM MissionResult mr JOIN Mission m ON mr.missionId = m.id WHERE m.type = :type")
    List<MissionResult> findByMissionType(@Param("type") Mission.MissionType type);
}