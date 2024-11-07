package com.ip.api.service;

import com.ip.api.apiPayload.code.ErrorCode;
import com.ip.api.apiPayload.exception.BadRequestException;
import com.ip.api.apiPayload.exception.NotFoundException;
import com.ip.api.domain.PwResetCodes;
import com.ip.api.domain.User;
import com.ip.api.domain.enums.UserStatus;
import com.ip.api.dto.user.UserRequest.PasswordDTO;
import com.ip.api.dto.user.UserRequest.ResetPwDTO;
import com.ip.api.dto.user.UserRequest.UserJoinDTO;
import com.ip.api.dto.user.UserResponse.EmailCodeDTO;
import com.ip.api.dto.user.UserResponse.ListForPaging;
import com.ip.api.dto.user.UserResponse.PasswordResult;
import com.ip.api.dto.user.UserResponse.UserDTO;
import com.ip.api.repository.PwResetCodesRepository;
import com.ip.api.repository.UserRepository;
import jakarta.mail.MessagingException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HRService {
    private final UserRepository userRepository;
    private final PwResetCodesRepository pwRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private static final long CODE_VALIDITY_DURATION_MINUTES = 10;

    public EmailCodeDTO sendVerificationCode(ResetPwDTO request) {
        User user = userRepository.findByEmail(request.getEmail());

        if (user == null) {
            throw new NotFoundException(ErrorCode.USER_NOT_FOUND);
        }

        if (!user.getEmail().equals(request.getEmail())) {
            throw new NotFoundException(ErrorCode.USER_VERIFICATION_FAIL);
        }
        String resetCode = generateRandomCode();
        try {
            emailService.sendPasswordResetCodeEmail(resetCode, user.getEmail(), user.getUserName()); // 이메일 발송 메서드 호출
        } catch (MessagingException e) {
            throw new BadRequestException(ErrorCode.USER_EMAIL_SEND_FAIL);
        }

        PwResetCodes addResetCode = PwResetCodes.builder()
                .email(request.getEmail())
                .verificationCode(resetCode)
                .expirationTime(LocalDateTime.now())
                .build();

        pwRepository.save(addResetCode);

        return EmailCodeDTO.builder()
                .code(resetCode)
                .build();
    }

    public PasswordResult changePassword(PasswordDTO request) {
        User user = userRepository.findByEmail(request.getEmail());

        // 인증코드 유효시간 검증
        PwResetCodes pwResetCodes = pwRepository.findByVerificationCode(request.getCode());

        if (pwResetCodes == null) {
            throw new NotFoundException(ErrorCode.CODE_NOT_FOUND);
        }

        if (!pwResetCodes.getVerificationCode().equals(request.getCode())) {
            throw new BadRequestException(ErrorCode.USER_AUTHENTICATION_FAIL);
        }

        long minutesElaspsed = ChronoUnit.MINUTES.between(pwResetCodes.getCreatedAt(), LocalDateTime.now());
        if (minutesElaspsed > CODE_VALIDITY_DURATION_MINUTES) {
            throw new BadRequestException(ErrorCode.CODE_EXPIRATION_TIME);
        }

        if (!request.isValidPassword()) {
            throw new BadRequestException(ErrorCode.USER_PASSWORD_INVALID);
        }
        // 새로운 User 객체 생성
        User updatedUser = User.builder()
                .userId(user.getUserId())
                .connId(user.getConnId())
                .email(user.getEmail())
                .userName(user.getUserName())
                .birth(user.getBirth())
                .userPhone(user.getUserPhone())
                .hireDate(user.getHireDate())
                .jobTitle(user.getJobTitle())
                .department(user.getDepartment())
                .address(user.getAddress())
                .role(user.getRole())
                .password(passwordEncoder.encode(request.getNewPassword()))
                .userStatus(user.getUserStatus())
                .build();

        // 새 User 객체 저장
        userRepository.save(updatedUser);

        return PasswordResult.builder()
                .userId(updatedUser.getUserId())
                .build();
    }

    public ListForPaging getUserList(User user, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> response = userRepository.findAll(pageable);

        // User 엔티티를 UserDTO로 변환
        List<UserDTO> userDTOList = response.getContent().stream()
                .map(userEntity -> new UserDTO(
                        userEntity.getUserId(),
                        userEntity.getUserName(),
                        userEntity.getAddress(),
                        userEntity.getEmail(),
                        userEntity.getDepartment(),
                        userEntity.getBirth(),
                        userEntity.getHireDate(),
                        userEntity.getJobTitle(),
                        userEntity.getUserPhone(),
                        userEntity.getUserStatus()
                ))
                .collect(Collectors.toList());

        // ListForPaging 객체 생성
        return ListForPaging.<UserDTO>builder()
                .totalElements(response.getTotalElements())
                .totalPages(response.getTotalPages())
                .pageSize(response.getSize())
                .currentPage(response.getNumber())
                .items(userDTOList)
                .build();
    }

    @Transactional
    public UserDTO updateUserInfo(long userId, UserJoinDTO request) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

        User updateUser = User.builder()
                .userId(existingUser.getUserId())
                .userName(request.getName())
                .email(request.getEmail())
                .birth(existingUser.getBirth())
                .hireDate(existingUser.getHireDate())
                .connId(existingUser.getConnId())
                .role(existingUser.getRole())
                .address(request.getAddress())
                .password(existingUser.getPassword())
                .department(request.getDepartment())
                .jobTitle(request.getJobTitle())
                .userPhone(request.getUserPhone())
                .userStatus(request.getUserStatus())
                .build();

        userRepository.save(updateUser);

        return UserDTO.builder()
                .userIdx(updateUser.getUserId())
                .build();
    }

    public UserDTO deleteUserInfo(long userId) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

        userRepository.delete(existingUser);
        return UserDTO.builder()
                .userIdx(existingUser.getUserId())
                .build();
    }

    public UserDTO getUserInfo(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
        return UserDTO.builder()
                .userIdx(user.getUserId())
                .name(user.getUserName())
                .userPhone(user.getUserPhone())
                .department(user.getDepartment())
                .jobTitle(user.getJobTitle())
                .birth(user.getBirth())
                .hireDate(user.getHireDate())
                .email(user.getEmail())
                .userStatus(user.getUserStatus())
                .address(user.getAddress())
                .build();
    }

    private String generateRandomCode() {
        SecureRandom random = new SecureRandom();
        int code = 100000 + random.nextInt(900000); // 100000~999999 범위 내 숫자 생성
        return String.valueOf(code);
    }

    public UserDTO updateUserStatus(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
        User updateUser = User.builder()
                .userId(user.getUserId())
                .userName(user.getUserName())
                .email(user.getEmail())
                .birth(user.getBirth())
                .hireDate(user.getHireDate())
                .connId(user.getConnId())
                .role(user.getRole())
                .address(user.getAddress())
                .password(user.getPassword())
                .department(user.getDepartment())
                .jobTitle(user.getJobTitle())
                .userPhone(user.getUserPhone())
                .userStatus(UserStatus.퇴사)
                .build();
        userRepository.save(updateUser);

        return UserDTO.builder()
                .userIdx(updateUser.getUserId())
                .build();
    }
}
