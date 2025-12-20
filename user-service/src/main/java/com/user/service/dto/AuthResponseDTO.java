package com.user.service.dto;

public class AuthResponseDTO {
    private String accessToken;
    private String tokenType = "Bearer";
    private long expiresInSeconds;
    private UserInfoDTO user;

    public AuthResponseDTO(String accessToken, long expiresInSeconds, UserInfoDTO user) {
        this.accessToken = accessToken;
        this.expiresInSeconds = expiresInSeconds;
        this.user = user;
    }

    public String getAccessToken() { return accessToken; }
    public String getTokenType() { return tokenType; }
    public long getExpiresInSeconds() { return expiresInSeconds; }
    public UserInfoDTO getUser() { return user; }
}
