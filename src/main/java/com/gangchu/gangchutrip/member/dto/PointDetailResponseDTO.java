package com.gangchu.gangchutrip.member.dto;

import com.gangchu.gangchutrip.global.entity.PointHistory;
import java.util.Date;

public record PointDetailResponseDTO(Date historyDate, String reason, Integer amount) {

    public static PointDetailResponseDTO from(PointHistory history) {
        return new PointDetailResponseDTO(
            history.getHistoryDate(),
            history.getReason(),
            history.getAmount()
        );
    }
}
