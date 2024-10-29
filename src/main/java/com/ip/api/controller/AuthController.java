package com.ip.api.controller;

import com.ip.api.apiPayload.code.ApiResponse;
import com.ip.api.dto.user.UserRequest.UserJoinDTO;
import com.ip.api.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    //회원가입(직원 등록)
    @PostMapping("/join")
    public ApiResponse<String> join(@RequestBody UserJoinDTO request) {
        authService.join(request);
        return ApiResponse.of("회원가입 성공");
    }
}
