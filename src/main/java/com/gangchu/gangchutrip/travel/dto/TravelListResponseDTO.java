package com.gangchu.gangchutrip.travel.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TravelListResponseDTO {
    List<TravelListContentDTO> content;
    private Integer totalElements;
}
