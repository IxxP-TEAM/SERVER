package com.ip.api.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    public static class UserJoinDTO {
        @Email(message = "이메일 형식이 아닙니다.")
        @JsonProperty("email")
        private String email;
        @NotBlank(message = "이름은 필수 입력 값입니다.")
        private String name;
        private AddressDTO address;
        private String birth;
        private String userPhone;
        private LocalDate hireDate;
        private String jobTitle;
        private String department;
        private String userStatus;
    }
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddressDTO {
        private String street;
        private String city;
        private String state;
        private int zipCode;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PasswordDTO {
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
}
