package com.ip.api.controller;

import com.ip.api.apiPayload.code.ApiResponse;
import com.ip.api.auth.AuthUser;
import com.ip.api.domain.User;
import com.ip.api.dto.user.UserResponse.CheckInStatusDTO;
import com.ip.api.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {
    private final AttendanceService attendanceService;
    @GetMapping("/status")
    public ApiResponse<CheckInStatusDTO> checkInStatus(@AuthUser User user) {
        CheckInStatusDTO response = attendanceService.checkInStatus(user);
        return ApiResponse.of(response);
    }

    // 출근하기
    @PostMapping("")
    public ApiResponse<CheckInStatusDTO> checkIn(@AuthUser User user) {
        CheckInStatusDTO response = attendanceService.checkIn(user);
        return ApiResponse.of(response);
    }
}
