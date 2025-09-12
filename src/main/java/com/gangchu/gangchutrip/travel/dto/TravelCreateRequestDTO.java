package com.gangchu.gangchutrip.travel.dto;

import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TravelCreateRequestDTO {

    private String title;
    private String travelWith;
    private Integer accompanyNum;
    private Date startDate;
    private Date endDate;
    private Integer mainThemeId;
    private List<Integer> subThemeIds;
}
