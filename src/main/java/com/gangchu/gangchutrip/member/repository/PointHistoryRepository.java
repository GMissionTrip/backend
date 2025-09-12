package com.gangchu.gangchutrip.member.repository;

import com.gangchu.gangchutrip.global.entity.PointHistory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointHistoryRepository extends JpaRepository<PointHistory, Long> {

    List<PointHistory> findAllByMemberId(Long memberId);

    List<PointHistory> findAllByMemberIdOrderByHistoryDateDesc(Long id);
}
