package com.ip.api.service;

import com.ip.api.apiPayload.code.ErrorCode;
import com.ip.api.apiPayload.exception.BadRequestException;
import com.ip.api.config.security.CustomUserDetails;
import com.ip.api.config.security.JwtTokenDto;
import com.ip.api.config.security.JwtUtil;
import com.ip.api.domain.User;
import com.ip.api.dto.user.UserRequest.LoginDTO;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final AuthenticationManager authenticationManager;
    private final RedisTemplate<String, String> redisTemplate;

    private final JwtUtil jwtUtil;

    public JwtTokenDto login(LoginDTO request) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(request.getConnId(), request.getPassword());
        try {
            Authentication auth = authenticationManager.authenticate(authToken);  //인증 실패 시 예외처리

            CustomUserDetails customUserDetails = (CustomUserDetails) auth.getPrincipal();
            String username = customUserDetails.getUsername();

            Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
            Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
            GrantedAuthority authority = iterator.next();
            String role = authority.getAuthority();

            return jwtUtil.generateToken(username, role);
        } catch (BadCredentialsException e) {
            throw new BadRequestException(ErrorCode.USER_BAD_CREDENTIAL);  // 잘못된 자격 증명
        } catch (AuthenticationException e) {
            throw new BadRequestException(ErrorCode.USER_AUTHENTICATION_FAIL);  // 기타 인증 오류
        }
    }

    public void logout(HttpServletRequest request, User user) {
        // 요청 헤더에서 액세스 토큰 가져오기
        String token = jwtUtil.resolveToken(request);

        if (token != null && jwtUtil.validateToken(token)) {
            // 토큰 만료 시간 계산
            long expiration = jwtUtil.getExpiration(token);

            // Redis에 토큰 블랙리스트 추가 (만료 시간 설정)
            redisTemplate.opsForValue().set(token, "logout", expiration, TimeUnit.MILLISECONDS);
        }
    }
}
