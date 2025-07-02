package com.gangchu.gangchutrip.auth.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AuthResponseDto {

    @JsonProperty("succeed")
    private boolean succeed;

    @JsonProperty("accessToken")
    private String accessToken;

    @JsonProperty("refreshToken")
    private String refreshToken;
}
