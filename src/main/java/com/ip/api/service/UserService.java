package com.ip.api.service;

import com.ip.api.apiPayload.code.ErrorCode;
import com.ip.api.apiPayload.exception.BadRequestException;
import com.ip.api.domain.Address;
import com.ip.api.domain.User;
import com.ip.api.domain.enums.Department;
import com.ip.api.domain.enums.Role;
import com.ip.api.domain.enums.UserStatus;
import com.ip.api.dto.user.UserRequest.UserJoinDTO;
import com.ip.api.repository.AddressRepository;
import com.ip.api.repository.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final EmailService emailService;

    public void join(UserJoinDTO request) {

        // 이메일 발송
        String connId = generatedId(request.getEmail());
        try {
            emailService.sendEmail(connId, request.getEmail(), request.getName());
        } catch (MessagingException e) {
            throw new BadRequestException(ErrorCode.USER_EMAIL_SEND_FAIL);
        }

        Address address = Address.builder()
                .street(request.getAddress().getStreet())
                .city(request.getAddress().getCity())
                .state(request.getAddress().getState())
                .zipCode(request.getAddress().getZipCode())
                .build();

        Address savedAddress = addressRepository.save(address);

        User user = User.builder()
                .email(request.getEmail())
                .userName(request.getName())
                .connId(connId)
                .address(savedAddress)
                .birth(request.getBirth())
                .userPhone(request.getUserPhone())
                .hireDate(request.getHireDate())
                .jobTitle(request.getJobTitle())
                .department(Department.valueOf(request.getDepartment()))
                .password("0000")
                .role(Role.ROLE_USER)
                .userStatus(UserStatus.valueOf(request.getUserStatus()))
                .build();

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new BadRequestException(ErrorCode.USER_EMAIL_ALREADY_EXISTS);
        }
        userRepository.save(user);
    }

    //아이디 생성
    private String generatedId(String email) {
        int idx = email.indexOf("@");
        String connId = email.substring(0, idx);
        return connId;
    }
}