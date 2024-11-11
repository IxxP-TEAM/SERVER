package com.ip.api.service;

import com.ip.api.domain.Leaves;
import com.ip.api.domain.User;
import com.ip.api.domain.enums.ApprovalStatus;
import com.ip.api.dto.leave.LeaveRequest.CreateLeaveDTO;
import com.ip.api.dto.user.UserResponse.PasswordResult;
import com.ip.api.repository.LeaveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LeaveService {
    private final LeaveRepository leaveRepository;

    public PasswordResult createLeave(User user, CreateLeaveDTO request) {
        Leaves leaves = Leaves.builder()
                .leaveType(request.getLeaveType())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .reason(request.getReason())
                .approvalStatus(ApprovalStatus.INACTIVE)
                .user(user)
                .build();

        Leaves newLeaves = leaveRepository.save(leaves);
        return PasswordResult.builder()
                .userId(newLeaves.getUser().getUserId())
                .build();
    }
}
