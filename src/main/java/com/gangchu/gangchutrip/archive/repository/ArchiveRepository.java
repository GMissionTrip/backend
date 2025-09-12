package com.gangchu.gangchutrip.archive.repository;

import com.gangchu.gangchutrip.global.entity.Archive;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArchiveRepository extends JpaRepository<Archive, Long> {

    List<Archive> findAllByMemberId(Long memberId);
}
