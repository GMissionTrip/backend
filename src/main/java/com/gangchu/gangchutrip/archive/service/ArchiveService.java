package com.gangchu.gangchutrip.archive.service;

import com.gangchu.gangchutrip.archive.dto.ArchivesResponseDTO;
import com.gangchu.gangchutrip.archive.repository.ArchiveRepository;
import com.gangchu.gangchutrip.archive.repository.TravelRepository;
import com.gangchu.gangchutrip.auth.entity.Member;
import com.gangchu.gangchutrip.auth.repository.MemberRepository;
import com.gangchu.gangchutrip.global.entity.Archive;
import com.gangchu.gangchutrip.global.entity.Travel;
import com.gangchu.gangchutrip.global.response.ApiResponseFactory;
import com.gangchu.gangchutrip.global.response.ResponseCode;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ArchiveService {

    private final ArchiveRepository archiveRepository;
    private final TravelRepository travelRepository;
    private final MemberRepository memberRepository;

    public ResponseEntity<?> getArchives(String username) {
        Member m = memberRepository.findByEmail(username);
        List<Archive> archives = archiveRepository.findAllByMemberId(m.getId());
        // Travel 에서 title, startDate, endDate 를 archive에 있는 travelId로 조회 어떤 한 값이 null이면 넘기기
        List<ArchivesResponseDTO> responseDTOs = archives.stream().map(archive -> {
            Travel travel = travelRepository.findById(archive.getTravelId()).orElse(null);
            if (travel != null) {
                return new ArchivesResponseDTO(archive.getId(), travel.getTitle(), travel.getStartDate(), travel.getEndDate());
            } else {
                return null; // travel이 null인 경우 null 반환
            }
        }).toList();
        return ApiResponseFactory.success(ResponseCode.OK, responseDTOs);
    }

    public ResponseEntity<?> getArchivesDetail(String username, Long archiveId) {
        Member m = memberRepository.findByEmail(username);
        Archive archive = archiveRepository.findById(archiveId).orElse(null);
        if (archive == null || !archive.getMemberId().equals(m.getId())) {
            return ApiResponseFactory.error(ResponseCode.NOT_FOUND,
                "Archive not found or access denied");
        }
        Travel travel = travelRepository.findById(archive.getTravelId()).orElse(null);
        if (travel == null) {
            return ApiResponseFactory.error(ResponseCode.NOT_FOUND,
                "Travel not found for the given archive");
        }
        ArchivesResponseDTO responseDTO = new ArchivesResponseDTO(archive.getId(), travel.getTitle(), travel.getStartDate(), travel.getEndDate());
        return ApiResponseFactory.success(ResponseCode.OK, responseDTO);
    }
}
