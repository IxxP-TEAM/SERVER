package com.ip.api.controller;

import com.ip.api.apiPayload.code.ApiResponse;
import com.ip.api.auth.AuthUser;
import com.ip.api.domain.User;
import com.ip.api.dto.user.UserResponse.ListForPaging;
import com.ip.api.service.HRService;
import com.ip.api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/hr")
@RequiredArgsConstructor
public class HRController {

    private final HRService hrService;

    // 직원 목록 리스트 조회
    @GetMapping("")
    public ApiResponse<ListForPaging> getUserList(@AuthUser User user,
                                                  @RequestParam(defaultValue = "0")int page,
                                                  @RequestParam(defaultValue = "10")int size) {
        ListForPaging response = hrService.getUserList(user, page, size);
        return ApiResponse.of(response);
    }
}
