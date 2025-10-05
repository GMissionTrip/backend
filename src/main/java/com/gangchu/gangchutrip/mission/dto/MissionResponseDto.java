package com.gangchu.gangchutrip.mission.dto;

import com.gangchu.gangchutrip.mission.entity.Mission;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MissionResponseDto {
    private Long id;
    private Long travelId;
    private String title;
    private String description;
    private String type;
    private String status;
    private Integer points;
    private String icon;
    private Integer order;
    private String requirements;
    private String submittedData;
    private String location;
    private String category;
    
    public static MissionResponseDto from(Mission mission) {
        return MissionResponseDto.builder()
                .id(mission.getId())
                .travelId(mission.getTravel().getId())
                .title(mission.getTitle())
                .description(mission.getDescription())
                .type(mission.getType().name())
                .status(mission.getStatus().name())
                .points(mission.getPoints())
                .icon(mission.getIcon())
                .order(mission.getOrder())
                .requirements(mission.getRequirements())
                .submittedData(mission.getSubmittedData())
                .location(mission.getLocation())
                .category(mission.getCategory())
                .build();
    }
}

