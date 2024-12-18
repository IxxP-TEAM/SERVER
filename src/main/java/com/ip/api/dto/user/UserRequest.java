package com.ip.api.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ip.api.domain.enums.Department;
import com.ip.api.domain.enums.UserStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UserRequest {
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class UserJoinDTO {
        @Email(message = "이메일 형식이 아닙니다.")
        @JsonProperty("email")
        private String email;
        @NotBlank(message = "이름은 필수 입력 값입니다.")
        private String name;
        private String address;
        private String birth;
        private String userPhone;
        private LocalDate hireDate;
        private String jobTitle;
        private Department department;
        private UserStatus userStatus;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PasswordDTO {
        private String email;
        private String code;
        @Size(max = 8, message = "비밀번호는 8자 이상이어야 합니다.")
        @JsonProperty("newPassword")
        private String newPassword;

        public boolean isValidPassword() {
            int cnt = 0;

            if (this.newPassword.matches(".*[A-Za-z].*")) {
                cnt++;
            }
            if (this.newPassword.matches(".*\\d.*")) {
                cnt ++;
            }
            if (this.newPassword.matches(".*\\W.*")) {
                cnt++;
            }

            return cnt >= 2;
        }
    }
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginDTO {
        private String connId;
        private String password;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResetPwDTO {
        @JsonProperty("email")
        private String email;
    }
}
