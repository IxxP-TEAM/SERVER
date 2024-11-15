package com.ip.api.dto.leave;

import com.ip.api.domain.enums.ApprovalStatus;
import com.ip.api.domain.enums.LeaveType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class LeaveResponse {
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LeaveListDTO {
        private long leaveId;
        private String username;
        private LeaveType leaveType;
        private String date;
        private ApprovalStatus status;
    }
}
