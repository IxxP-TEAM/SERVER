package com.ip.api.service;

import com.ip.api.apiPayload.code.ErrorCode;
import com.ip.api.apiPayload.exception.BadRequestException;
import com.ip.api.config.security.CustomUserDetails;
import com.ip.api.config.security.JwtTokenDto;
import com.ip.api.config.security.JwtUtil;
import com.ip.api.domain.Address;
import com.ip.api.domain.User;
import com.ip.api.domain.enums.Department;
import com.ip.api.domain.enums.Role;
import com.ip.api.domain.enums.UserStatus;
import com.ip.api.dto.user.UserRequest.LoginDTO;
import com.ip.api.dto.user.UserRequest.PasswordDTO;
import com.ip.api.dto.user.UserRequest.UserJoinDTO;
import com.ip.api.dto.user.UserResponse.PasswordResult;
import com.ip.api.repository.AddressRepository;
import com.ip.api.repository.UserRepository;
import jakarta.mail.MessagingException;
import java.util.Collection;
import java.util.Iterator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;

    public PasswordResult changePassword(User user, PasswordDTO request) {
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

    public JwtTokenDto login(LoginDTO request) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(request.getConnId(), request.getPassword());
        try {
            Authentication auth = authenticationManager.authenticate(authToken);  //인증 실패 시 예외처리

            CustomUserDetails customUserDetails = (CustomUserDetails) auth.getPrincipal();
            String username = customUserDetails.getUsername();

            Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
            Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
            GrantedAuthority authority = iterator.next();
            String role = authority.getAuthority();

            return jwtUtil.generateToken(username, role);
        } catch (BadCredentialsException e) {
            throw new BadRequestException(ErrorCode.USER_BAD_CREDENTIAL);  // 잘못된 자격 증명
        } catch (AuthenticationException e) {
            throw new BadRequestException(ErrorCode.USER_AUTHENTICATION_FAIL);  // 기타 인증 오류
        }
    }
}
