package com.gangchu.gangchutrip.test;

import com.gangchu.gangchutrip.archive.repository.TravelRepository;
import com.gangchu.gangchutrip.global.entity.Travel;
import com.gangchu.gangchutrip.global.response.ApiResponse;
import com.gangchu.gangchutrip.global.response.ResponseCode;
import com.gangchu.gangchutrip.global.security.jwt.MemberPrincipal;
import com.gangchu.gangchutrip.auth.entity.Member;
import com.gangchu.gangchutrip.auth.repository.MemberRepository;
import com.gangchu.gangchutrip.mission.dto.MissionResponseDto;
import com.gangchu.gangchutrip.mission.dto.SubmitMissionRequestDto;
import com.gangchu.gangchutrip.mission.entity.Mission;
import com.gangchu.gangchutrip.mission.repository.MissionRepository;
import com.gangchu.gangchutrip.mission.service.MissionService;
import com.gangchu.gangchutrip.global.entity.PointHistory;
import com.gangchu.gangchutrip.global.repository.PointHistoryRepository;
import com.gangchu.gangchutrip.travel.dto.CreateTravelRequestDto;
import com.gangchu.gangchutrip.travel.dto.TravelResponseDto;
import com.gangchu.gangchutrip.travel.service.TravelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/test/user-flow")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "유저 플로우 테스트", description = "전체 유저 플로우를 테스트하는 API")
public class UserFlowTestController {
    
    private final MemberRepository memberRepository;
    private final TravelService travelService;
    private final MissionService missionService;
    private final MissionRepository missionRepository;
    private final TravelRepository travelRepository;
    private final PointHistoryRepository pointHistoryRepository;
    
    @PostMapping("/complete-flow")
    @Operation(summary = "전체 플로우 테스트", 
               description = "회원가입부터 여행 완료까지 전체 플로우를 한번에 테스트합니다.")
    public ResponseEntity<ApiResponse<Map<String, Object>>> testCompleteFlow(
            @AuthenticationPrincipal MemberPrincipal principal) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 1단계: 회원 정보 확인
            Long memberId = Long.parseLong(principal.getUsername());
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다"));
            
            log.info("=== 1단계: 회원 정보 확인 ===");
            log.info("회원 ID: {}, 닉네임: {}, 이메일: {}", 
                    member.getId(), member.getNickname(), member.getEmail());
            result.put("step1_member", Map.of(
                    "id", member.getId(),
                    "nickname", member.getNickname(),
                    "email", member.getEmail(),
                    "totalPoints", member.getTotalPoints()
            ));
            
            // 2단계: 여행 생성
            log.info("=== 2단계: 여행 생성 ===");
            CreateTravelRequestDto travelRequest = new CreateTravelRequestDto(
                    "강원도 가족 여행",
                    "강원도",
                    new Date(),
                    new Date(System.currentTimeMillis() + 5 * 24 * 60 * 60 * 1000), // 5일 후
                    "FAMILY",
                    4,
                    "자연과 힐링",
                    "맛집 탐방"
            );
            
            TravelResponseDto travel = travelService.createTravel(travelRequest, memberId);
            log.info("여행 생성 완료 - ID: {}, 제목: {}", travel.getId(), travel.getTitle());
            result.put("step2_travel", travel);
            
            // 3단계: 미션 목록 확인
            log.info("=== 3단계: 자동 생성된 미션 확인 ===");
            List<MissionResponseDto> missions = missionService.getTravelMissions(travel.getId());
            log.info("생성된 미션 수: {}", missions.size());
            missions.forEach(m -> log.info("- 미션: {} ({}P)", m.getTitle(), m.getPoints()));
            result.put("step3_missions", missions);
            
            // 4단계: 여행 시작
            log.info("=== 4단계: 여행 시작 ===");
            TravelResponseDto startedTravel = travelService.startTravel(travel.getId(), memberId);
            log.info("여행 상태: {} -> {}", travel.getStatus(), startedTravel.getStatus());
            result.put("step4_started_travel", startedTravel);
            
            // 5단계: 첫 번째 미션 수행 (사진 업로드 미션)
            log.info("=== 5단계: 미션 수행 ===");
            if (!missions.isEmpty()) {
                MissionResponseDto firstMission = missions.get(0);
                SubmitMissionRequestDto submission = new SubmitMissionRequestDto(
                        Arrays.asList(
                                "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=400",
                                "https://images.unsplash.com/photo-1579548122080-c35fd6820ecb?w=400",
                                "https://images.unsplash.com/photo-1602002413757-3c6010dd1b7c?w=400"
                        ),
                        null,
                        null,
                        null
                );
                
                MissionResponseDto completedMission = missionService.submitMission(
                        firstMission.getId(), 
                        submission, 
                        memberId
                );
                
                log.info("미션 완료: {} - {}P 획득", completedMission.getTitle(), completedMission.getPoints());
                result.put("step5_completed_mission", completedMission);
                
                // 포인트 히스토리 확인
                Member updatedMember = memberRepository.findById(memberId).orElseThrow();
                log.info("획득한 총 포인트: {}", updatedMember.getTotalPoints());
                result.put("step5_total_points", updatedMember.getTotalPoints());
            }
            
            // 6단계: 포인트 히스토리 확인
            log.info("=== 6단계: 포인트 히스토리 확인 ===");
            List<PointHistory> pointHistories = pointHistoryRepository.findByMemberIdOrderByHistoryDateDesc(memberId);
            log.info("포인트 히스토리 건수: {}", pointHistories.size());
            pointHistories.forEach(ph -> log.info("- {}: {}P", ph.getReason(), ph.getAmount()));
            result.put("step6_point_history", pointHistories);
            
            // 7단계: 여행 완료
            log.info("=== 7단계: 여행 완료 ===");
            TravelResponseDto completedTravel = travelService.completeTravel(travel.getId(), memberId);
            log.info("여행 상태: {} -> {}", startedTravel.getStatus(), completedTravel.getStatus());
            result.put("step7_completed_travel", completedTravel);
            
            // 8단계: 최종 통계
            log.info("=== 8단계: 최종 통계 ===");
            long completedMissionsCount = missionRepository.countByTravelIdAndStatus(
                    travel.getId(), 
                    Mission.MissionStatus.COMPLETED
            );
            long totalMissionsCount = missionRepository.countByTravelId(travel.getId());
            
            Member finalMember = memberRepository.findById(memberId).orElseThrow();
            
            Map<String, Object> finalStats = new HashMap<>();
            finalStats.put("completedMissions", completedMissionsCount);
            finalStats.put("totalMissions", totalMissionsCount);
            finalStats.put("completionRate", (completedMissionsCount * 100.0 / totalMissionsCount));
            finalStats.put("totalPoints", finalMember.getTotalPoints());
            finalStats.put("level", finalMember.getLevel());
            
            log.info("완료 미션: {}/{}", completedMissionsCount, totalMissionsCount);
            log.info("완료율: {}%", completedMissionsCount * 100.0 / totalMissionsCount);
            log.info("총 포인트: {}P", finalMember.getTotalPoints());
            
            result.put("step8_final_stats", finalStats);
            
            result.put("success", true);
            result.put("message", "전체 유저 플로우 테스트 완료!");
            
            log.info("=== 유저 플로우 테스트 완료 ===");
            
        } catch (Exception e) {
            log.error("유저 플로우 테스트 중 오류 발생", e);
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        
        return ResponseEntity.ok(ApiResponse.success(ResponseCode.SUCCESS, result));
    }
    
    @GetMapping("/my-data")
    @Operation(summary = "내 데이터 조회", description = "현재 로그인한 사용자의 모든 데이터를 조회합니다.")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getMyData(
            @AuthenticationPrincipal MemberPrincipal principal) {
        
        Long memberId = Long.parseLong(principal.getUsername());
        Map<String, Object> data = new HashMap<>();
        
        // 회원 정보
        Member member = memberRepository.findById(memberId).orElseThrow();
        data.put("member", member);
        
        // 여행 목록
        List<Travel> travels = travelRepository.findByMemberIdOrderByCreatedDateDesc(memberId);
        data.put("travels", travels);
        
        // 모든 미션
        List<Mission> allMissions = new ArrayList<>();
        for (Travel travel : travels) {
            allMissions.addAll(missionRepository.findByTravelIdOrderByOrderAsc(travel.getId()));
        }
        data.put("missions", allMissions);
        
        // 포인트 히스토리
        List<PointHistory> pointHistories = pointHistoryRepository.findByMemberIdOrderByHistoryDateDesc(memberId);
        data.put("pointHistory", pointHistories);
        
        return ResponseEntity.ok(ApiResponse.success(ResponseCode.SUCCESS, data));
    }
}

