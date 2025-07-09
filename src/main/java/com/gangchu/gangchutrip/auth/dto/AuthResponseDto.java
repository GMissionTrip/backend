package com.gangchu.gangchutrip.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponseDto {

    @Schema(description = "access token")
    private String accessToken;

    @Schema(description = "refresh token")
    private String refreshToken;
}
