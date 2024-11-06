package com.ip.api.controller;

import com.ip.api.apiPayload.code.ApiResponse;
import com.ip.api.auth.AuthUser;
import com.ip.api.domain.User;
import com.ip.api.dto.user.UserRequest.PasswordDTO;
import com.ip.api.dto.user.UserRequest.UserJoinDTO;
import com.ip.api.dto.user.UserResponse.ListForPaging;
import com.ip.api.dto.user.UserResponse.PasswordResult;
import com.ip.api.dto.user.UserResponse.UserDTO;
import com.ip.api.service.HRService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    // 직원 정보 수정
    @PatchMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<UserDTO> updateUserInfo(@AuthUser User user, @PathVariable long userId, @RequestBody UserJoinDTO request) {
        UserDTO response = hrService.updateUserInfo(userId, request);
        return ApiResponse.of(response);
    }

    // 직원 정보 삭제
    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<UserDTO> deleteUserInfo(@AuthUser User user, @PathVariable long userId) {
        UserDTO response = hrService.deleteUserInfo(userId);
        return ApiResponse.of(response);
    }

    // 직원 상세 정보 조회
    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<UserDTO> getUserInfo(@AuthUser User user, @PathVariable long userId) {
        UserDTO response = hrService.getUserInfo(userId);
        return ApiResponse.of(response);
    }

    //사용자 비밀번호 설정
    @PostMapping("/resetPassword")
    public ApiResponse<PasswordResult> changePassword(@AuthUser User user, @RequestBody PasswordDTO request) {
        PasswordResult response = hrService.changePassword(user, request);
        return ApiResponse.of(response);
    }
}
