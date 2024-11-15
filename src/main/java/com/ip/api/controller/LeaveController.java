package com.ip.api.controller;

import com.ip.api.apiPayload.code.ApiResponse;
import com.ip.api.auth.AuthUser;
import com.ip.api.domain.User;
import com.ip.api.dto.leave.LeaveRequest.CreateLeaveDTO;
import com.ip.api.dto.leave.LeaveRequest.RefuseLeaveDTO;
import com.ip.api.dto.user.UserResponse.ListForPaging;
import com.ip.api.dto.user.UserResponse.PasswordResult;
import com.ip.api.service.LeaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/leave")
@RequiredArgsConstructor
public class LeaveController {
    private final LeaveService leaveService;

    @PostMapping("")
    public ApiResponse<PasswordResult> createLeave(@AuthUser User user, @RequestBody CreateLeaveDTO request) {
        PasswordResult response = leaveService.createLeave(user, request);
        return ApiResponse.of(response);
    }

    // 휴가 승인하기
    @PatchMapping("/approval/{leaveId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<PasswordResult> approve(@PathVariable long leaveId) {
        PasswordResult response = leaveService.approve(leaveId);
        return ApiResponse.of(response);
    }

    // 휴가 거절하기
    @PatchMapping("/refusal/{leaveId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<PasswordResult> refuse(@PathVariable long leaveId, @RequestBody RefuseLeaveDTO request) {
        PasswordResult response = leaveService.refuse(leaveId, request);
        return ApiResponse.of(response);
    }

    // 휴가 목록 리스트 조회
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<ListForPaging> getLeaveList(@RequestParam(defaultValue = "0")int page,
                                                   @RequestParam(defaultValue = "10")int size) {
        ListForPaging response = leaveService.getLeaveList(page, size);
        return ApiResponse.of(response);
    }
}
