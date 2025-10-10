package com.gangchu.gangchutrip.mission.text.service;

import com.gangchu.gangchutrip.archive.repository.ArchiveRepository;
import com.gangchu.gangchutrip.auth.entity.Member;
import com.gangchu.gangchutrip.auth.repository.MemberRepository;
import com.gangchu.gangchutrip.global.entity.Archive;
import com.gangchu.gangchutrip.global.entity.Mission;
import com.gangchu.gangchutrip.global.entity.MissionResult;
import com.gangchu.gangchutrip.global.response.ApiResponse;
import com.gangchu.gangchutrip.global.response.ResponseCode;

import com.gangchu.gangchutrip.mission.text.dto.TemperatureMissionRequestDto;
import com.gangchu.gangchutrip.mission.text.dto.TemperatureMissionResponseDto;
import com.gangchu.gangchutrip.mission.repository.MissionRepository;
import com.gangchu.gangchutrip.mission.repository.MissionResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TemperatureMissionService {

    private final MissionRepository missionRepository;
    private final MissionResultRepository missionResultRepository;
    private final MemberRepository memberRepository;
    private final ArchiveRepository archiveRepository;

    // 임의의 미션 아이디값 지정
    private static final Long TEMPERATURE_MISSION_ID = 3L;

    private static final List<Integer> VALID_TEMPERATURES = Arrays.asList(20, 40, 60, 80, 100);

    @Transactional
    public ResponseEntity<ApiResponse<TemperatureMissionResponseDto>> submitTemperatureMission(TemperatureMissionRequestDto requestDto) {

        if (!VALID_TEMPERATURES.contains(requestDto.getTemperature())) {
            return ResponseEntity
                    .status(ResponseCode.BAD_REQUEST.getStatus())
                    .body(ApiResponse.error(ResponseCode.BAD_REQUEST, "온도는 20, 40, 60, 80, 100 중 하나여야 합니다."));
        }

        String travelId = requestDto.getRouteId().toString();

        boolean exists = missionResultRepository.existsByMissionIdAndTouristIdAndTravelIdAndMemberId(
                TEMPERATURE_MISSION_ID,
                requestDto.getTouristId(),
                travelId,
                requestDto.getMemberId()
        );

        if (exists) {
            return ResponseEntity
                    .status(ResponseCode.DUPLICATED_RESOURCE.getStatus())
                    .body(ApiResponse.error(ResponseCode.DUPLICATED_RESOURCE, "이미 해당 관광지에 기분 온도계를 작성하셨습니다."));
        }

        Member member = memberRepository.findById(requestDto.getMemberId())
                .orElse(null);
        if (member == null) {
            return ResponseEntity
                    .status(ResponseCode.NOT_FOUND.getStatus())
                    .body(ApiResponse.error(ResponseCode.NOT_FOUND, "회원이 존재하지 않습니다."));
        }

        Mission mission = missionRepository.findById(TEMPERATURE_MISSION_ID)
                .orElse(null);
        if (mission == null) {
            return ResponseEntity
                    .status(ResponseCode.NOT_FOUND.getStatus())
                    .body(ApiResponse.error(ResponseCode.NOT_FOUND, "기분 온도계 미션이 존재하지 않습니다."));
        }

        MissionResult missionResult = new MissionResult();
        missionResult.setMissionId(TEMPERATURE_MISSION_ID);
        missionResult.setMemberId(member.getId());
        missionResult.setTouristId(requestDto.getTouristId());
        missionResult.setTravelId(travelId);
        missionResult.setContext(requestDto.getTemperature().toString());
        missionResult.setIsCompleted(true);

        MissionResult saved = missionResultRepository.save(missionResult);

        if (!archiveRepository.existsByMemberIdAndTravelId(member.getId(), requestDto.getRouteId())) {
            Archive archive = new Archive();
            archive.setMemberId(member.getId());
            archive.setTravelId(requestDto.getRouteId());
            archiveRepository.save(archive);
        }

        TemperatureMissionResponseDto response = TemperatureMissionResponseDto.builder()
                .id(saved.getId())
                .temperature(Integer.parseInt(saved.getContext()))
                .temperatureLevel(getTemperatureLevel(Integer.parseInt(saved.getContext())))
                .touristId(saved.getTouristId())
                .routeId(requestDto.getRouteId())
                .memberId(saved.getMemberId())
                .missionCompleted(true)
                .build();

        return ResponseEntity.ok(ApiResponse.success(ResponseCode.OK, response));
    }

    private String getTemperatureLevel(Integer temperature) {
        switch (temperature) {
            case 20:
                return "아쉬워요";
            case 40:
                return "보통이에요";
            case 60:
                return "좋아요";
            case 80:
                return "정말 좋아요";
            case 100:
                return "최고에요!";
            default:
                return "알 수 없음";
        }
    }
}