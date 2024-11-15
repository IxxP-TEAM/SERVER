package com.ip.api.service;

import com.ip.api.apiPayload.code.ErrorCode;
import com.ip.api.apiPayload.exception.NotFoundException;
import com.ip.api.domain.Leaves;
import com.ip.api.domain.User;
import com.ip.api.domain.enums.ApprovalStatus;
import com.ip.api.dto.leave.LeaveRequest.CreateLeaveDTO;
import com.ip.api.dto.leave.LeaveRequest.RefuseLeaveDTO;
import com.ip.api.dto.user.UserResponse.PasswordResult;
import com.ip.api.repository.LeaveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                .approvalStatus(ApprovalStatus.대기)
                .user(user)
                .build();

        Leaves newLeaves = leaveRepository.save(leaves);
        return PasswordResult.builder()
                .userId(newLeaves.getUser().getUserId())
                .build();
    }

    public PasswordResult approve(long leaveId) {
        Leaves leave = leaveRepository.findById(leaveId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.LEAVE_NOT_FOUND));

        Leaves ApproveLeave = Leaves.builder()
                .leaveId(leave.getLeaveId())
                .leaveType(leave.getLeaveType())
                .startDate(leave.getStartDate())
                .endDate(leave.getEndDate())
                .reason(leave.getReason())
                .user(leave.getUser())
                .approvalStatus(ApprovalStatus.승인)
                .inactiveReason(null)
                .build();
        leaveRepository.save(ApproveLeave);

        return PasswordResult.builder()
                .userId(ApproveLeave.getUser().getUserId())
                .build();
    }

    @Transactional
    public PasswordResult refuse(long leaveId, RefuseLeaveDTO request) {
        Leaves leave = leaveRepository.findById(leaveId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.LEAVE_NOT_FOUND));

        Leaves RefuseLeave = Leaves.builder()
                .leaveId(leave.getLeaveId())
                .leaveType(leave.getLeaveType())
                .startDate(leave.getStartDate())
                .endDate(leave.getEndDate())
                .reason(leave.getReason())
                .user(leave.getUser())
                .approvalStatus(ApprovalStatus.거절)
                .inactiveReason(request.getRefuseReason())
                .build();
        leaveRepository.save(RefuseLeave);

        return PasswordResult.builder()
                .userId(RefuseLeave.getUser().getUserId())
                .build();
    }
}
