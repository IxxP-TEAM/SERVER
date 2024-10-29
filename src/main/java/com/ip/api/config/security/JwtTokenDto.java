package com.ip.api.config.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtTokenDto {
    private String grantType;  //JWT 인증 타입 - Bearer 인증 방식
    private String accessToken;
    private String refreshToken;
}
