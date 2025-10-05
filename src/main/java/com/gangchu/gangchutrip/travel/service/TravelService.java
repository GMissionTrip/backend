package com.gangchu.gangchutrip.travel.service;

import com.gangchu.gangchutrip.global.entity.Travel;
import com.gangchu.gangchutrip.global.exception.BaseException;
import com.gangchu.gangchutrip.global.response.ResponseCode;
import com.gangchu.gangchutrip.auth.entity.Member;
import com.gangchu.gangchutrip.auth.repository.MemberRepository;
import com.gangchu.gangchutrip.mission.service.MissionService;
import com.gangchu.gangchutrip.travel.dto.CreateTravelRequestDto;
import com.gangchu.gangchutrip.travel.dto.TravelResponseDto;
import com.gangchu.gangchutrip.archive.repository.TravelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TravelService {
    
    private final TravelRepository travelRepository;
    private final MemberRepository memberRepository;
    private final MissionService missionService;
    
    // 여행 생성
    @Transactional
    public TravelResponseDto createTravel(CreateTravelRequestDto request, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND));
        
        Travel travel = new Travel();
        travel.setMemberId(memberId);
        travel.setTitle(request.getTitle());
        travel.setRegion(request.getRegion());
        travel.setStartDate(request.getStartDate());
        travel.setEndDate(request.getEndDate());
        travel.setAccompanyNum(request.getAccompanyNum());
        travel.setMainTheme(request.getMainTheme());
        travel.setSubTheme(request.getSubTheme());
        travel.setCreatedDate(new Date());
        travel.setModifiedDate(new Date());
        travel.setStatus(Travel.TravelStatus.PLANNING);
        
        // TravelWith enum 설정
        if (request.getTravelWith() != null) {
            try {
                travel.setTravelWith(Travel.TravelWith.valueOf(request.getTravelWith()));
            } catch (IllegalArgumentException e) {
                travel.setTravelWith(Travel.TravelWith.OTHER);
            }
        }
        
        // 커버 이미지 기본값
        travel.setCoverImage("https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=800");
        
        Travel savedTravel = travelRepository.save(travel);
        
        // 미션 자동 생성
        missionService.generateMissionsForTravel(savedTravel);
        
        log.info("여행 생성 완료 - travelId: {}, memberId: {}, title: {}", 
                savedTravel.getId(), memberId, savedTravel.getTitle());
        
        return TravelResponseDto.from(savedTravel);
    }
    
    // 여행 시작
    @Transactional
    public TravelResponseDto startTravel(Long travelId, Long memberId) {
        Travel travel = travelRepository.findById(travelId)
                .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND));
        
        if (!travel.getMemberId().equals(memberId)) {
            throw new BaseException(ResponseCode.FORBIDDEN);
        }
        
        travel.setStatus(Travel.TravelStatus.IN_PROGRESS);
        travel.setModifiedDate(new Date());
        
        Travel savedTravel = travelRepository.save(travel);
        
        log.info("여행 시작 - travelId: {}, memberId: {}", travelId, memberId);
        
        return TravelResponseDto.from(savedTravel);
    }
    
    // 여행 완료
    @Transactional
    public TravelResponseDto completeTravel(Long travelId, Long memberId) {
        Travel travel = travelRepository.findById(travelId)
                .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND));
        
        if (!travel.getMemberId().equals(memberId)) {
            throw new BaseException(ResponseCode.FORBIDDEN);
        }
        
        travel.setStatus(Travel.TravelStatus.COMPLETED);
        travel.setModifiedDate(new Date());
        
        Travel savedTravel = travelRepository.save(travel);
        
        log.info("여행 완료 - travelId: {}, memberId: {}", travelId, memberId);
        
        return TravelResponseDto.from(savedTravel);
    }
    
    // 내 여행 목록 조회
    @Transactional(readOnly = true)
    public List<TravelResponseDto> getMyTravels(Long memberId) {
        List<Travel> travels = travelRepository.findByMemberIdOrderByCreatedDateDesc(memberId);
        return travels.stream()
                .map(TravelResponseDto::from)
                .collect(Collectors.toList());
    }
    
    // 여행 상세 조회
    @Transactional(readOnly = true)
    public TravelResponseDto getTravel(Long travelId) {
        Travel travel = travelRepository.findById(travelId)
                .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND));
        return TravelResponseDto.from(travel);
    }
}
