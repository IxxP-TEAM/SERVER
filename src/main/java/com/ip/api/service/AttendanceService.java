package com.ip.api.service;

import com.ip.api.domain.Attendence;
import com.ip.api.domain.User;
import com.ip.api.domain.enums.Status;
import com.ip.api.dto.user.UserResponse.AttendanceStatusDTO;
import com.ip.api.repository.AttendanceRepository;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AttendanceService {
    private final AttendanceRepository attendanceRepository;

    public AttendanceStatusDTO checkInStatus(User user) {
        Attendence attendence = attendanceRepository.findByUser(user);

        boolean isCheckIn = attendence != null;
        System.out.println(isCheckIn);

        return AttendanceStatusDTO.builder()
                .status(attendence.getAttStatus())
                .build();
    }

    public AttendanceStatusDTO checkIn(User user) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalTime lateTime = LocalTime.of(9, 0);
        boolean isLate = currentDateTime.toLocalTime().isAfter(lateTime);

        Attendence attendence = Attendence.builder()
                .user(user)
                .checkInTime(LocalDateTime.now())
                .attStatus(Status.ACTIVE)
                .checkOutTime(null)
                .lateFlag(isLate)
                .earlyLeaveFlag(false)
                .build();

        attendanceRepository.save(attendence);
        return AttendanceStatusDTO.builder()
                .status(attendence.getAttStatus())
                .build();
    }
}
