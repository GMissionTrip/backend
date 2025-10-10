package com.gangchu.gangchutrip.travel.dto;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TravelListContentDTO {

    private Long id;
    private Long memberId;
    private String title;
    private Date startDate;
    private Date endDate;

}
