package com.gangchu.gangchutrip.auth.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class KakaoTokenResponse {

    @JsonProperty("access_token")
    private String access_token;

    @JsonProperty("token_type")
    private String token_type;

    @JsonProperty("expires_in")
    private Integer expires_in;

    @JsonProperty("refresh_token")
    private String refresh_token;

    @JsonProperty("scope")
    private String scope;

    @JsonProperty("refresh_token_expires_in")
    private Integer refresh_token_expires_in;

}
