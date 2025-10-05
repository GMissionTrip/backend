package com.gangchu.gangchutrip.mission.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SubmitMissionRequestDto {
    private List<String> photos;
    private String text;
    private Integer temperature;
    private String selectedOption;
}

