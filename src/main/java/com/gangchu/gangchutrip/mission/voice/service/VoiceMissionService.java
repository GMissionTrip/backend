package com.gangchu.gangchutrip.mission.voice.service;

import com.gangchu.gangchutrip.archive.repository.ArchiveRepository;
import com.gangchu.gangchutrip.auth.entity.Member;
import com.gangchu.gangchutrip.auth.repository.MemberRepository;
import com.gangchu.gangchutrip.global.entity.Archive;
import com.gangchu.gangchutrip.global.entity.Mission;
import com.gangchu.gangchutrip.global.entity.MissionResult;
import com.gangchu.gangchutrip.global.response.ApiResponse;
import com.gangchu.gangchutrip.global.response.ResponseCode;


import com.gangchu.gangchutrip.mission.repository.MissionRepository;
import com.gangchu.gangchutrip.mission.repository.MissionResultRepository;
import com.gangchu.gangchutrip.mission.voice.dto.VoiceMissionRequestDto;
import com.gangchu.gangchutrip.mission.voice.dto.VoiceMissionResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class VoiceMissionService {

    private final MissionRepository missionRepository;
    private final MissionResultRepository missionResultRepository;
    private final ArchiveRepository archiveRepository;
    private final MemberRepository memberRepository;
    private final FileStorageService fileStorageService;

    private static final Long VOICE_MISSION_ID = 4L;

    @Transactional
    public ResponseEntity<ApiResponse<VoiceMissionResponseDto>> submitVoiceMission(VoiceMissionRequestDto requestDto) {

        try {
            validateVoiceFile(requestDto.getVoiceFile());

            String travelId = requestDto.getRouteId().toString();

            boolean exists = missionResultRepository.existsByMissionIdAndTouristIdAndTravelIdAndMemberId(
                    VOICE_MISSION_ID,
                    requestDto.getTouristId(),
                    travelId,
                    requestDto.getMemberId()
            );

            if (exists) {
                return ResponseEntity
                        .status(ResponseCode.DUPLICATED_RESOURCE.getStatus())
                        .body(ApiResponse.error(ResponseCode.DUPLICATED_RESOURCE, "이미 해당 관광지에 음성 미션을 완료하셨습니다."));
            }

            Member member = memberRepository.findById(requestDto.getMemberId())
                    .orElse(null);
            if (member == null) {
                return ResponseEntity
                        .status(ResponseCode.NOT_FOUND.getStatus())
                        .body(ApiResponse.error(ResponseCode.NOT_FOUND, "회원이 존재하지 않습니다."));
            }

            Mission mission = missionRepository.findById(VOICE_MISSION_ID)
                    .orElse(null);
            if (mission == null) {
                return ResponseEntity
                        .status(ResponseCode.NOT_FOUND.getStatus())
                        .body(ApiResponse.error(ResponseCode.NOT_FOUND, "음성 미션이 존재하지 않습니다."));
            }

            if (mission.getType() != Mission.MissionType.VOICE) {
                return ResponseEntity
                        .status(ResponseCode.BAD_REQUEST.getStatus())
                        .body(ApiResponse.error(ResponseCode.BAD_REQUEST, "음성 타입 미션이 아닙니다."));
            }

            String resultUrl = fileStorageService.saveVoiceFile(
                    requestDto.getVoiceFile(),
                    requestDto.getMemberId(),
                    VOICE_MISSION_ID
            );

            MissionResult missionResult = new MissionResult();
            missionResult.setMissionId(VOICE_MISSION_ID);
            missionResult.setMemberId(member.getId());
            missionResult.setTouristId(requestDto.getTouristId());
            missionResult.setTravelId(travelId);
            String contextWithUrl = "VOICE_URL:" + resultUrl;
            if (requestDto.getContext() != null && !requestDto.getContext().isEmpty()) {
                contextWithUrl += "|CONTEXT:" + requestDto.getContext();
            }
            missionResult.setContext(contextWithUrl);
            missionResult.setIsCompleted(true);

            MissionResult saved = missionResultRepository.save(missionResult);

            if (!archiveRepository.existsByMemberIdAndTravelId(member.getId(), requestDto.getRouteId())) {
                Archive archive = new Archive();
                archive.setMemberId(member.getId());
                archive.setTravelId(requestDto.getRouteId());
                archiveRepository.save(archive);
            }

            String savedResultUrl = extractUrlFromContext(saved.getContext());
            String savedContext = extractContextFromContext(saved.getContext());

            VoiceMissionResponseDto response = VoiceMissionResponseDto.builder()
                    .id(saved.getId())
                    .resultUrl(savedResultUrl)
                    .context(savedContext)
                    .touristId(saved.getTouristId())
                    .routeId(requestDto.getRouteId())
                    .memberId(saved.getMemberId())
                    .missionCompleted(true)
                    .build();

            return ResponseEntity.ok(ApiResponse.success(ResponseCode.OK, response));

        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(ResponseCode.BAD_REQUEST.getStatus())
                    .body(ApiResponse.error(ResponseCode.BAD_REQUEST, e.getMessage()));
        } catch (IOException e) {
            return ResponseEntity
                    .status(ResponseCode.INTERNAL_SERVER_ERROR.getStatus())
                    .body(ApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "파일 저장 중 오류가 발생했습니다."));
        } catch (Exception e) {
            return ResponseEntity
                    .status(ResponseCode.INTERNAL_SERVER_ERROR.getStatus())
                    .body(ApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다."));
        }
    }

    private void validateVoiceFile(MultipartFile voiceFile) {
        if (voiceFile == null || voiceFile.isEmpty()) {
            throw new IllegalArgumentException("음성 파일이 필요합니다.");
        }

        if (!isValidAudioFile(voiceFile)) {
            throw new IllegalArgumentException("지원하지 않는 음성 파일 형식입니다. (mp3, wav, ogg만 지원)");
        }

        if (voiceFile.getSize() > 10 * 1024 * 1024) {
            throw new IllegalArgumentException("음성 파일 크기는 10MB를 초과할 수 없습니다.");
        }
    }

    private boolean isValidAudioFile(MultipartFile file) {
        String contentType = file.getContentType();
        String filename = file.getOriginalFilename();

        if (contentType != null) {
            return contentType.equals("audio/mpeg") ||
                    contentType.equals("audio/wav") ||
                    contentType.equals("audio/ogg");
        }

        if (filename != null) {
            return filename.toLowerCase().endsWith(".mp3") ||
                    filename.toLowerCase().endsWith(".wav") ||
                    filename.toLowerCase().endsWith(".ogg");
        }

        return false;
    }

    private String extractUrlFromContext(String context) {
        if (context == null) return null;
        if (context.startsWith("VOICE_URL:")) {
            int pipeIndex = context.indexOf("|");
            if (pipeIndex > 0) {
                return context.substring("VOICE_URL:".length(), pipeIndex);
            } else {
                return context.substring("VOICE_URL:".length());
            }
        }
        return null;
    }

    private String extractContextFromContext(String context) {
        if (context == null) return null;
        int contextIndex = context.indexOf("|CONTEXT:");
        if (contextIndex > 0) {
            return context.substring(contextIndex + "|CONTEXT:".length());
        }
        return null;
    }
}