package com.gangchu.gangchutrip.travel.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateTravelRequestDto {
    private String title;
    private String region;
    private Date startDate;
    private Date endDate;
    private String travelWith; // ALONE, FRIENDS, FAMILY, COUPLE, OTHER
    private Integer accompanyNum;
    private String mainTheme;
    private String subTheme;
}

