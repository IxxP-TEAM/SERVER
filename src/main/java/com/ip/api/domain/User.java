package com.ip.api.domain;

import com.ip.api.domain.common.BaseEntity;
import com.ip.api.domain.enums.Department;
import com.ip.api.domain.enums.Role;
import com.ip.api.domain.enums.UserStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;    //유저 인덱스
    private String connId;  //유저 접속 아이디
    private String password;
    private String userName;
    private String email;
    private String birth;
    private String userPhone;
    private LocalDate hireDate;
    private String jobTitle;
    private UserStatus userStatus;
    private Department department;
    @OneToOne
    @JoinColumn(name = "address_id", referencedColumnName = "addressId")
    private Address address;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;
}