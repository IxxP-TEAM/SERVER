package com.ip.api.controller;

import com.ip.api.apiPayload.code.ApiResponse;
import com.ip.api.dto.user.UserRequest.UserJoinDTO;
import com.ip.api.service.UserService;
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

    //회원가입
    @PostMapping("/join")
    public ApiResponse<String> join(@RequestBody UserJoinDTO request) {
        userService.join(request);
        return ApiResponse.of("회원가입 성공");
    }
}
