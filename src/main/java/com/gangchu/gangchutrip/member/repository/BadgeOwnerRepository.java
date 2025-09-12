package com.gangchu.gangchutrip.member.repository;

import com.gangchu.gangchutrip.global.entity.BadgeOwner;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BadgeOwnerRepository extends JpaRepository<BadgeOwner, Long> {

    List<BadgeOwner> findAllByMemberId(Long memberId);
}
