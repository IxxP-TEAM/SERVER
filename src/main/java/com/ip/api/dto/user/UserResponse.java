package com.ip.api.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ip.api.domain.enums.Department;
import com.ip.api.domain.enums.UserStatus;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UserResponse {
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PasswordResult {
        Long userId;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ListForPaging<T> {
        private long totalElements;
        private int totalPages;
        private int pageSize;
        private int currentPage;
        private List<T> items;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class UserDTO {
        private long userIdx;
        private String name;
        private String address;
        private String email;
        private Department department;
        private String birth;
        private LocalDate hireDate;
        private String jobTitle;
        private String userPhone;
        private UserStatus userStatus;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class EmailCodeDTO {
        private String code;
    }
}
