package com.ip.api.controller;

import com.ip.api.apiPayload.code.ApiResponse;
import com.ip.api.auth.AuthUser;
import com.ip.api.config.security.JwtTokenDto;
import com.ip.api.domain.User;
import com.ip.api.dto.user.UserRequest.LoginDTO;
import com.ip.api.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    // 로그인
    @PostMapping("/login")
    public ApiResponse<JwtTokenDto> login(@RequestBody LoginDTO request) {
        JwtTokenDto response = userService.login(request);
        return ApiResponse.of(response);
    }

    // 로그아웃
    @PostMapping("/logout")
    public ApiResponse<String> logout(HttpServletRequest request, @AuthUser User user) {
        userService.logout(request, user);
        return ApiResponse.of("로그아웃 성공");
    }
}
