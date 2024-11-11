package com.ip.api.dto.leave;

import com.ip.api.domain.enums.LeaveType;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class LeaveRequest {
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateLeaveDTO {
        private LeaveType leaveType;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private String reason;
    }
}
