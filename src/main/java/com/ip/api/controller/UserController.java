package com.ip.api.controller;

import com.ip.api.apiPayload.code.ApiResponse;
import com.ip.api.auth.AuthUser;
import com.ip.api.config.security.JwtTokenDto;
import com.ip.api.domain.User;
import com.ip.api.dto.user.UserRequest.LoginDTO;
import com.ip.api.dto.user.UserRequest.PasswordDTO;
import com.ip.api.dto.user.UserResponse.PasswordResult;
import com.ip.api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    //사용자 비밀번호 설정
    @PostMapping("/resetPassword")
    public ApiResponse<PasswordResult> changePassword(@AuthUser User user, @RequestBody PasswordDTO request) {
        PasswordResult response = userService.changePassword(user, request);
        return ApiResponse.of(response);
    }

    // 로그인
    @PostMapping("/login")
    public ApiResponse<JwtTokenDto> login(@RequestBody LoginDTO request) {
        JwtTokenDto response = userService.login(request);
        return ApiResponse.of(response);
    }
}
