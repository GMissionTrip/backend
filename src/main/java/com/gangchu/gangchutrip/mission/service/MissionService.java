package com.gangchu.gangchutrip.mission.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gangchu.gangchutrip.global.exception.BaseException;
import com.gangchu.gangchutrip.global.response.ResponseCode;
import com.gangchu.gangchutrip.auth.entity.Member;
import com.gangchu.gangchutrip.auth.repository.MemberRepository;
import com.gangchu.gangchutrip.mission.dto.MissionResponseDto;
import com.gangchu.gangchutrip.mission.dto.SubmitMissionRequestDto;
import com.gangchu.gangchutrip.mission.entity.Mission;
import com.gangchu.gangchutrip.mission.repository.MissionRepository;
import com.gangchu.gangchutrip.global.entity.PointHistory;
import com.gangchu.gangchutrip.global.repository.PointHistoryRepository;
import com.gangchu.gangchutrip.global.entity.Travel;
import com.gangchu.gangchutrip.archive.repository.TravelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MissionService {
    
    private final MissionRepository missionRepository;
    private final TravelRepository travelRepository;
    private final MemberRepository memberRepository;
    private final PointHistoryRepository pointHistoryRepository;
    private final ObjectMapper objectMapper;
    
    // 여행의 미션 목록 조회
    @Transactional(readOnly = true)
    public List<MissionResponseDto> getTravelMissions(Long travelId) {
        List<Mission> missions = missionRepository.findByTravelIdOrderByOrderAsc(travelId);
        return missions.stream()
                .map(MissionResponseDto::from)
                .collect(Collectors.toList());
    }
    
    // 미션 상세 조회
    @Transactional(readOnly = true)
    public MissionResponseDto getMission(Long missionId) {
        Mission mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND));
        return MissionResponseDto.from(mission);
    }
    
    // 미션 제출
    @Transactional
    public MissionResponseDto submitMission(Long missionId, SubmitMissionRequestDto request, Long memberId) {
        Mission mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND));
        
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND));
        
        // 제출 데이터를 JSON으로 저장
        try {
            Map<String, Object> submissionData = new HashMap<>();
            if (request.getPhotos() != null) {
                submissionData.put("photos", request.getPhotos());
            }
            if (request.getText() != null) {
                submissionData.put("text", request.getText());
            }
            if (request.getTemperature() != null) {
                submissionData.put("temperature", request.getTemperature());
            }
            if (request.getSelectedOption() != null) {
                submissionData.put("selectedOption", request.getSelectedOption());
            }
            submissionData.put("submittedAt", LocalDateTime.now().toString());
            
            String jsonData = objectMapper.writeValueAsString(submissionData);
            mission.setSubmittedData(jsonData);
            mission.setStatus(Mission.MissionStatus.COMPLETED);
            
            // 포인트 지급
            if (mission.getPoints() > 0) {
                member.setTotalPoints(member.getTotalPoints() + mission.getPoints());
                memberRepository.save(member);
                
                // 포인트 히스토리 저장
                PointHistory pointHistory = new PointHistory();
                pointHistory.setMember(member);
                pointHistory.setAmount(mission.getPoints());
                pointHistory.setReason("미션 완료: " + mission.getTitle());
                pointHistory.setHistoryDate(LocalDateTime.now());
                pointHistoryRepository.save(pointHistory);
            }
            
            Mission savedMission = missionRepository.save(mission);
            
            log.info("미션 제출 완료 - missionId: {}, memberId: {}, points: {}", 
                    missionId, memberId, mission.getPoints());
            
            return MissionResponseDto.from(savedMission);
            
        } catch (Exception e) {
            log.error("미션 제출 중 오류 발생", e);
            throw new BaseException(ResponseCode.INTERNAL_SERVER_ERROR);
        }
    }
    
    // 미션 잠금 해제
    @Transactional
    public MissionResponseDto unlockMission(Long missionId) {
        Mission mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND));
        
        mission.setStatus(Mission.MissionStatus.UNLOCKED);
        Mission savedMission = missionRepository.save(mission);
        
        return MissionResponseDto.from(savedMission);
    }
    
    // 여행 생성 시 자동으로 미션 생성
    @Transactional
    public void generateMissionsForTravel(Travel travel) {
        // 기본 미션들 생성
        createMission(travel, 1, "3컷 여행 요약", "오늘 하루를 3장으로 요약해 사진을 업로드해세요!", 
                Mission.MissionType.PHOTO_UPLOAD, "📸", 250, "가벼운");
        
        createMission(travel, 2, "감정 온도계", "지금 당신의 여행 만족도는 몇 도인가요?", 
                Mission.MissionType.TEMPERATURE, "🌡️", 120, "가벼운");
        
        createMission(travel, 3, "같이찍Go", "사랑하는 가족과 함께 사진을 찍어보세요!", 
                Mission.MissionType.PHOTO_UPLOAD, "📷", 150, "가벼운");
        
        createMission(travel, 4, "나의 하루 요약하기", "여행 중 있었던 기억 나는 일들을 요약해보세요!", 
                Mission.MissionType.TEXT_INPUT, "📝", 100, "가벼운");
        
        createMission(travel, 5, "랜덤 챌린지", "오늘의 깜짝 미션을 수행해보세요!", 
                Mission.MissionType.RANDOM_CHALLENGE, "🎲", 180, "가벼운");
        
        createMission(travel, 6, "여행 베컷 요약", "여행 중 찍은 베스트 사진들을 업로드하세요!", 
                Mission.MissionType.FILM_PHOTOS, "🎞️", 150, "가벼운");
        
        log.info("여행 ID {}에 대한 미션 생성 완료", travel.getId());
    }
    
    private void createMission(Travel travel, int order, String title, String description,
                               Mission.MissionType type, String icon, int points, String category) {
        Mission mission = new Mission();
        mission.setTravel(travel);
        mission.setTitle(title);
        mission.setDescription(description);
        mission.setType(type);
        mission.setIcon(icon);
        mission.setPoints(points);
        mission.setOrder(order);
        mission.setCategory(category);
        mission.setStatus(Mission.MissionStatus.UNLOCKED);
        
        // Requirements JSON 생성
        try {
            Map<String, Object> requirements = new HashMap<>();
            if (type == Mission.MissionType.PHOTO_UPLOAD || type == Mission.MissionType.FILM_PHOTOS) {
                requirements.put("photoCount", 3);
            } else if (type == Mission.MissionType.TEXT_INPUT) {
                requirements.put("minTextLength", 10);
                requirements.put("maxTextLength", 500);
            } else if (type == Mission.MissionType.TEMPERATURE) {
                requirements.put("temperatureRange", new int[]{20, 100});
            }
            
            String jsonRequirements = objectMapper.writeValueAsString(requirements);
            mission.setRequirements(jsonRequirements);
        } catch (Exception e) {
            log.error("Requirements JSON 생성 실패", e);
        }
        
        missionRepository.save(mission);
    }
}

