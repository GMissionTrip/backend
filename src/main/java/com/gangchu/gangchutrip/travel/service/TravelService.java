package com.gangchu.gangchutrip.travel.service;


import com.gangchu.gangchutrip.archive.repository.TravelRepository;
import com.gangchu.gangchutrip.auth.entity.Member;
import com.gangchu.gangchutrip.auth.repository.MemberRepository;
import com.gangchu.gangchutrip.global.entity.Travel;
import com.gangchu.gangchutrip.global.response.ApiResponseFactory;
import com.gangchu.gangchutrip.global.response.ResponseCode;
import com.gangchu.gangchutrip.travel.dto.TravelCreateRequestDTO;
import com.gangchu.gangchutrip.travel.dto.TravelListContentDTO;
import com.gangchu.gangchutrip.travel.dto.TravelListResponseDTO;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TravelService {

    MemberRepository memberRepository;
    TravelRepository travelRepository;

    public ResponseEntity<?> createTravel(String username, TravelCreateRequestDTO dto) {
        Long uid = memberRepository.findByEmail(username).getId();
        Travel travel = Travel.builder()
            .title(dto.getTitle())
            .travelWith(dto.getTravelWith())
            .accompanyNum(dto.getAccompanyNum())
            .startDate(dto.getStartDate())
            .endDate(dto.getEndDate())
            .mainTheme(String.valueOf(dto.getMainThemeId()))
            .subTheme(String.join(",", dto.getSubThemeIds().stream().map(String::valueOf).toList()))
            .memberId(uid)
            .build();
        try {
            travelRepository.save(travel);
        } catch (Exception e) {
            return ApiResponseFactory.error(ResponseCode.INTERNAL_SERVER_ERROR, "여행 생성에 실패했습니다.");
        }
        return ApiResponseFactory.success(ResponseCode.CREATED);
    }

    public ResponseEntity<?> getAllTravel(String username) {
        Long uid = memberRepository.findByEmail(username).getId();
        try {
            List<Travel> travels = travelRepository.findAllByMemberId(uid);

            if (travels.isEmpty()) {
                return ApiResponseFactory.success(ResponseCode.OK,
                    new TravelListResponseDTO(List.of(), 0));
            }
            int totalElements = travels.size();
            List<TravelListContentDTO> content = travels.stream()
                .map(t -> new TravelListContentDTO(t.getId(), t.getMemberId(), t.getTitle(),
                    t.getStartDate(),
                    t.getEndDate()))
                .toList();
            TravelListResponseDTO responseDTO = new TravelListResponseDTO(content, totalElements);
            return ApiResponseFactory.success(ResponseCode.OK, responseDTO);
        } catch (Exception e) {
            return ApiResponseFactory.error(ResponseCode.INTERNAL_SERVER_ERROR, "여행 목록 조회에 실패했습니다.");
        }
    }

    public ResponseEntity<?> getTravelDetail(String username, Long travelId) {
        Long uid = memberRepository.findByEmail(username).getId();
        try {
            Travel travel = travelRepository.findByIdAndMemberId(travelId, uid);
            if (travel == null) {
                return ApiResponseFactory.error(ResponseCode.NOT_FOUND, "해당 여행을 찾을 수 없습니다.");
            }
            return ApiResponseFactory.success(ResponseCode.OK, travel);
        } catch (Exception e) {
            return ApiResponseFactory.error(ResponseCode.INTERNAL_SERVER_ERROR, "여행 상세 조회에 실패했습니다.");
        }
    }
}
