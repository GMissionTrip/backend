package com.gangchu.gangchutrip.archive.repository;

import com.gangchu.gangchutrip.global.entity.Travel;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TravelRepository extends JpaRepository<Travel, Long> {

    public List<Travel> findAllByMemberId(Long memberId);

    Travel findByIdAndMemberId(Long id, Long memberId);
}
