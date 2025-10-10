package com.gangchu.gangchutrip.mission.text.service;

import com.gangchu.gangchutrip.archive.repository.ArchiveRepository;
import com.gangchu.gangchutrip.auth.entity.Member;
import com.gangchu.gangchutrip.auth.repository.MemberRepository;
import com.gangchu.gangchutrip.global.entity.Archive;
import com.gangchu.gangchutrip.global.entity.Mission;
import com.gangchu.gangchutrip.global.entity.MissionResult;
import com.gangchu.gangchutrip.global.response.ApiResponse;
import com.gangchu.gangchutrip.global.response.ResponseCode;

import com.gangchu.gangchutrip.mission.text.dto.ReverseMissionRequestDto;
import com.gangchu.gangchutrip.mission.text.dto.ReverseMissionResponseDto;
import com.gangchu.gangchutrip.mission.repository.MissionRepository;
import com.gangchu.gangchutrip.mission.repository.MissionResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class ReverseMissionService {

    private final MissionRepository missionRepository;
    private final MissionResultRepository missionResultRepository;
    private final ArchiveRepository archiveRepository;
    private final MemberRepository memberRepository;

    private static final Long REVERSE_MISSION_ID = 5L;

    private static final List<String> REVERSE_QUESTIONS = Arrays.asList(
            "이번 관광지에서 아쉬웠던 점은 무엇인가요?",
            "이번 관광지에서 개선되면 좋겠다고 생각하는 점은?",
            "이번 관광지 방문 전 기대와 다른 점이 있다면?",
            "이번 관광지에서 불편했던 점이 있다면 무엇인가요?",
            "이번 관광지의 아쉬운 부분을 한 가지만 말해주세요",
            "이번 관광지에서 예상과 달랐던 점은?",
            "이번 관광지 방문 시 아쉬웠던 순간이 있다면?",
            "이번 관광지에 다시 온다면 무엇이 달라지면 좋을까요?"
    );

    private final Random random = new Random();

    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<String>> getRandomQuestion() {
        String randomQuestion = REVERSE_QUESTIONS.get(random.nextInt(REVERSE_QUESTIONS.size()));
        return ResponseEntity.ok(ApiResponse.success(ResponseCode.OK, randomQuestion));
    }

    @Transactional
    public ResponseEntity<ApiResponse<ReverseMissionResponseDto>> submitReverseMission(ReverseMissionRequestDto requestDto) {

        if (requestDto.getQuestion() == null || !REVERSE_QUESTIONS.contains(requestDto.getQuestion())) {
            return ResponseEntity
                    .status(ResponseCode.BAD_REQUEST.getStatus())
                    .body(ApiResponse.error(ResponseCode.BAD_REQUEST, "유효하지 않은 질문입니다. 먼저 질문을 조회해주세요."));
        }

        String travelId = requestDto.getRouteId().toString();

        boolean exists = missionResultRepository.existsByMissionIdAndTouristIdAndTravelIdAndMemberId(
                REVERSE_MISSION_ID,
                requestDto.getTouristId(),
                travelId,
                requestDto.getMemberId()
        );

        if (exists) {
            return ResponseEntity
                    .status(ResponseCode.DUPLICATED_RESOURCE.getStatus())
                    .body(ApiResponse.error(ResponseCode.DUPLICATED_RESOURCE, "이미 해당 관광지에 반전 미션을 완료하셨습니다."));
        }

        Member member = memberRepository.findById(requestDto.getMemberId())
                .orElse(null);
        if (member == null) {
            return ResponseEntity
                    .status(ResponseCode.NOT_FOUND.getStatus())
                    .body(ApiResponse.error(ResponseCode.NOT_FOUND, "회원이 존재하지 않습니다."));
        }

        Mission mission = missionRepository.findById(REVERSE_MISSION_ID)
                .orElse(null);
        if (mission == null) {
            return ResponseEntity
                    .status(ResponseCode.NOT_FOUND.getStatus())
                    .body(ApiResponse.error(ResponseCode.NOT_FOUND, "반전 미션이 존재하지 않습니다."));
        }

        String contextWithQuestion = "질문: " + requestDto.getQuestion() + ", 답변: " + requestDto.getAnswer();

        MissionResult missionResult = new MissionResult();
        missionResult.setMissionId(REVERSE_MISSION_ID);
        missionResult.setMemberId(member.getId());
        missionResult.setTouristId(requestDto.getTouristId());
        missionResult.setTravelId(travelId);
        missionResult.setContext(contextWithQuestion);
        missionResult.setIsCompleted(true);

        MissionResult saved = missionResultRepository.save(missionResult);

        if (!archiveRepository.existsByMemberIdAndTravelId(member.getId(), requestDto.getRouteId())) {
            Archive archive = new Archive();
            archive.setMemberId(member.getId());
            archive.setTravelId(requestDto.getRouteId());
            archiveRepository.save(archive);
        }

        ReverseMissionResponseDto response = ReverseMissionResponseDto.builder()
                .id(saved.getId())
                .question(requestDto.getQuestion())
                .answer(requestDto.getAnswer())
                .touristId(saved.getTouristId())
                .routeId(requestDto.getRouteId())
                .memberId(saved.getMemberId())
                .missionCompleted(true)
                .build();

        return ResponseEntity.ok(ApiResponse.success(ResponseCode.OK, response));
    }
}