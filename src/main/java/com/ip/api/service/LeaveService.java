package com.ip.api.service;

import com.ip.api.apiPayload.code.ErrorCode;
import com.ip.api.apiPayload.exception.NotFoundException;
import com.ip.api.domain.Leaves;
import com.ip.api.domain.User;
import com.ip.api.domain.enums.ApprovalStatus;
import com.ip.api.dto.leave.LeaveRequest.CreateLeaveDTO;
import com.ip.api.dto.leave.LeaveRequest.RefuseLeaveDTO;
import com.ip.api.dto.leave.LeaveResponse.LeaveDetailDTO;
import com.ip.api.dto.leave.LeaveResponse.LeaveListDTO;
import com.ip.api.dto.user.UserResponse.ListForPaging;
import com.ip.api.dto.user.UserResponse.PasswordResult;
import com.ip.api.repository.LeaveRepository;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    public ListForPaging getLeaveList(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Leaves> response = leaveRepository.findAll(pageable);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        List<LeaveListDTO> leaveListDTO = response.getContent().stream()
                .map(leaveEntity -> {
                    String formattedDates = leaveEntity.getStartDate().format(formatter) +
                            " ~ " +
                            leaveEntity.getEndDate().format(formatter);
                    return new LeaveListDTO(
                            leaveEntity.getLeaveId(),
                            leaveEntity.getUser().getUserName(),
                            leaveEntity.getLeaveType(),
                            formattedDates,
                            leaveEntity.getApprovalStatus()
                    );
                })
                .collect(Collectors.toList());
        return ListForPaging.builder()
                .totalElements(response.getTotalElements())
                .totalPages(response.getTotalPages())
                .pageSize(response.getSize())
                .currentPage(response.getNumber())
                .items((List<Object>) (Object) leaveListDTO)
                .build();
    }

    public LeaveDetailDTO getLeaveDetail(long leaveId) {
        Leaves leaves = leaveRepository.findById(leaveId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.LEAVE_NOT_FOUND));
        return LeaveDetailDTO.builder()
                .leaveId(leaves.getLeaveId())
                .username(leaves.getUser().getUserName())
                .approvalStatus(leaves.getApprovalStatus())
                .startDate(leaves.getStartDate())
                .endDate(leaves.getEndDate())
                .leaveType(leaves.getLeaveType())
                .reason(leaves.getReason())
                .inactiveReason(leaves.getInactiveReason())
                .build();
    }
}
