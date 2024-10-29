package com.ip.api.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
}
