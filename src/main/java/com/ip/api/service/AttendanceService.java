package com.ip.api.service;

import com.ip.api.domain.Attendence;
import com.ip.api.domain.User;
import com.ip.api.domain.enums.Status;
import com.ip.api.dto.user.UserResponse.AttendanceStatusDTO;
import com.ip.api.repository.AttendanceRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;
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

    public AttendanceStatusDTO checkOut(User user) {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);
        Optional<Attendence> attendence = attendanceRepository.findByUserAndCheckInTimeBetween(user, startOfDay, endOfDay);

        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalTime standardLeave = LocalTime.of(18, 0);
        boolean isEarlyLeave = currentDateTime.toLocalTime().isBefore(standardLeave);

        Attendence leaveWork = Attendence.builder()
                .attId(attendence.get().getAttId())
                .user(user)
                .checkInTime(LocalDateTime.now())
                .attStatus(Status.INACTIVE)
                .checkOutTime(LocalDateTime.now())
                .lateFlag(attendence.get().isLateFlag())
                .earlyLeaveFlag(isEarlyLeave)
                .build();

        attendanceRepository.save(leaveWork);

        return AttendanceStatusDTO.builder()
                .status(leaveWork.getAttStatus())
                .build();
    }
}
