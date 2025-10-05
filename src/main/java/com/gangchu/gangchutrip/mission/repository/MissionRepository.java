package com.gangchu.gangchutrip.mission.repository;

import com.gangchu.gangchutrip.mission.entity.Mission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MissionRepository extends JpaRepository<Mission, Long> {
    List<Mission> findByTravelIdOrderByOrderAsc(Long travelId);
    long countByTravelIdAndStatus(Long travelId, Mission.MissionStatus status);
    long countByTravelId(Long travelId);
}

