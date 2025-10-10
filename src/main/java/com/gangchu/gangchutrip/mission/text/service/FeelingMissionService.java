package com.gangchu.gangchutrip.mission.text.service;

import com.gangchu.gangchutrip.archive.repository.ArchiveRepository;
import com.gangchu.gangchutrip.auth.entity.Member;
import com.gangchu.gangchutrip.auth.repository.MemberRepository;
import com.gangchu.gangchutrip.global.entity.Archive;
import com.gangchu.gangchutrip.global.entity.Mission;
import com.gangchu.gangchutrip.global.entity.MissionResult;
import com.gangchu.gangchutrip.global.response.ApiResponse;
import com.gangchu.gangchutrip.global.response.ApiResponseFactory;
import com.gangchu.gangchutrip.global.response.ResponseCode;

import com.gangchu.gangchutrip.mission.text.dto.FeelingMissionRequestDto;
import com.gangchu.gangchutrip.mission.text.dto.FeelingMissionResponseDto;
import com.gangchu.gangchutrip.mission.repository.MissionRepository;
import com.gangchu.gangchutrip.mission.repository.MissionResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FeelingMissionService {

    private final MissionRepository missionRepository;
    private final MissionResultRepository missionResultRepository;
    private final MemberRepository memberRepository;
    private final ArchiveRepository archiveRepository;

    //임의 미션 번호
    private static final Long FEELING_MISSION_ID = 2L;

    @Transactional
    public ResponseEntity<?> submitFeelingMission(FeelingMissionRequestDto requestDto) {

        String travelId = requestDto.getRouteId().toString();

        boolean exists = missionResultRepository.existsByMissionIdAndTouristIdAndTravelIdAndMemberId(
                FEELING_MISSION_ID,
                requestDto.getTouristSpotId(),
                travelId,
                requestDto.getMemberId()
        );

        if (exists) {
            return ResponseEntity
                    .status(ResponseCode.DUPLICATED_RESOURCE.getStatus())
                    .body(ApiResponse.error(ResponseCode.DUPLICATED_RESOURCE, "이미 해당 관광지에 기분을 남기셨습니다."));
        }

        Member member = memberRepository.findById(requestDto.getMemberId())
                .orElse(null);

        if (member == null) {
            return ResponseEntity
                    .status(ResponseCode.NOT_FOUND.getStatus())
                    .body(ApiResponse.error(ResponseCode.NOT_FOUND, "회원이 존재하지 않습니다."));
        }

        Mission mission = missionRepository.findById(FEELING_MISSION_ID)
                .orElse(null);

        if (mission == null) {
            return ResponseEntity
                    .status(ResponseCode.NOT_FOUND.getStatus())
                    .body(ApiResponse.error(ResponseCode.NOT_FOUND, "기분 미션이 존재하지 않습니다."));
        }

        MissionResult missionResult = new MissionResult();
        missionResult.setMissionId(FEELING_MISSION_ID);
        missionResult.setMemberId(member.getId());
        missionResult.setTouristId(requestDto.getTouristSpotId());
        missionResult.setTravelId(travelId);
        missionResult.setContext(requestDto.getContent());
        missionResult.setIsCompleted(true);

        MissionResult saved = missionResultRepository.save(missionResult);

        if (!archiveRepository.existsByMemberIdAndTravelId(member.getId(), requestDto.getRouteId())) {
            Archive archive = new Archive();
            archive.setMemberId(member.getId());
            archive.setTravelId(requestDto.getRouteId());
            archiveRepository.save(archive);
        }

        FeelingMissionResponseDto response = FeelingMissionResponseDto.builder()
                .id(saved.getId())
                .content(saved.getContext())
                .touristSpotId(saved.getTouristId())
                .routeId(requestDto.getRouteId())
                .memberId(saved.getMemberId())
                .missionCompleted(true)
                .build();

        return ResponseEntity.ok(ApiResponseFactory.success(ResponseCode.OK, response));
    }
}