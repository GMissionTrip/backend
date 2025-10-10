package com.gangchu.gangchutrip.mission.repository;

import com.gangchu.gangchutrip.global.entity.Mission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MissionRepository extends JpaRepository<Mission, Long> {

    @Query("SELECT m FROM Mission m WHERE m.type = :type")
    List<Mission> findByType(@Param("type") Mission.MissionType type);

    @Query("SELECT m FROM Mission m WHERE m.isSudden = :isSudden")
    List<Mission> findByIsSudden(@Param("isSudden") Boolean isSudden);
}