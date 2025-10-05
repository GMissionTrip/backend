package com.gangchu.gangchutrip.travel.dto;

import com.gangchu.gangchutrip.global.entity.Travel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TravelResponseDto {
    private Long id;
    private Long memberId;
    private String title;
    private String region;
    private Date startDate;
    private Date endDate;
    private String travelWith;
    private Integer accompanyNum;
    private String mainTheme;
    private String subTheme;
    private String status;
    private String coverImage;
    private Date createdDate;
    
    public static TravelResponseDto from(Travel travel) {
        return TravelResponseDto.builder()
                .id(travel.getId())
                .memberId(travel.getMemberId())
                .title(travel.getTitle())
                .region(travel.getRegion())
                .startDate(travel.getStartDate())
                .endDate(travel.getEndDate())
                .travelWith(travel.getTravelWith() != null ? travel.getTravelWith().name() : null)
                .accompanyNum(travel.getAccompanyNum())
                .mainTheme(travel.getMainTheme())
                .subTheme(travel.getSubTheme())
                .status(travel.getStatus() != null ? travel.getStatus().name() : "PLANNING")
                .coverImage(travel.getCoverImage())
                .createdDate(travel.getCreatedDate())
                .build();
    }
}

