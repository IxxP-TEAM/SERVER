package com.ip.api.service;

import com.ip.api.apiPayload.code.ErrorCode;
import com.ip.api.apiPayload.exception.NotFoundException;
import com.ip.api.domain.Attendence;
import com.ip.api.domain.User;
import com.ip.api.domain.enums.Status;
import com.ip.api.dto.user.UserResponse.AttendanceStatusDTO;
import com.ip.api.dto.user.UserResponse.ListForPaging;
import com.ip.api.dto.user.UserResponse.MonthlyAttendanceStatusDTO;
import com.ip.api.repository.AttendanceRepository;
import com.ip.api.repository.UserRepository;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AttendanceService {
    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;

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
        Optional<Attendence> attendence = attendanceRepository.findByUserAndCheckInTimeBetween(user, startOfDay,
                endOfDay);

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

    public ListForPaging getMonthlyAttendanceStatus(User user, long userId, int page, int size, int year, int month) {
        User targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

        Pageable pageable = PageRequest.of(page, size);

        Page<Attendence> response = attendanceRepository.findByUser(targetUser, pageable);

        List<MonthlyAttendanceStatusDTO> AttDTOList = response.getContent().stream()
                .filter(attendence -> {
                    LocalDateTime checkInTime = attendence.getCheckInTime();
                    return checkInTime != null && checkInTime.getYear() == year && checkInTime.getMonthValue() == month;
                })
                .map(attendence -> {
                    LocalDateTime checkInTime = attendence.getCheckInTime();
                    LocalDateTime checkOutTime = attendence.getCheckOutTime();

                    String workTime = getFormattedWorkTime(checkInTime, checkOutTime);

                    return new MonthlyAttendanceStatusDTO(
                            attendence.getUser().getUserId(),
                            attendence.getUser().getUserName(),
                            attendence.getUser().getJobTitle(),
                            attendence.getUser().getDepartment(),
                            formatDate(attendence.getCreatedAt()),
                            formatTime(checkInTime),
                            formatTime(checkOutTime),
                            workTime,
                            attendence.isLateFlag()
                    );
                })
                .collect(Collectors.toList());

        return ListForPaging.<MonthlyAttendanceStatusDTO>builder()
                .totalElements(AttDTOList.size())
                .totalPages((int) Math.ceil((double) AttDTOList.size() / size))
                .pageSize(size)
                .currentPage(page)
                .items(AttDTOList)
                .build();
    }

    private String formatDate(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
        return dateTime.format(formatter);
    }

    private String formatTime(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return dateTime.format(formatter);
    }

    public String getFormattedWorkTime(LocalDateTime checkInTime, LocalDateTime checkOutTime) {
        return calculateWorkTime(checkInTime, checkOutTime);
    }

    private String calculateWorkTime(LocalDateTime checkInTime, LocalDateTime checkOutTime) {
        String workingTime = "0시간 0분";
        if (checkInTime != null && checkOutTime != null) {
            Duration duration = Duration.between(checkInTime, checkOutTime);
            long hours = duration.toHours();
            long minutes = duration.toMinutes() % 60;

            if (minutes >= 30) {
                hours += 1;
                minutes = 0;
            }

            if (minutes == 0) {
                workingTime = hours + "시간";
            } else {
                workingTime = hours + "시간 " + minutes + "분";
            }
        }
        return workingTime;
    }
}
