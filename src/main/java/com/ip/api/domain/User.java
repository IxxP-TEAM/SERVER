package com.ip.api.domain;

import com.ip.api.domain.common.BaseEntity;
import com.ip.api.domain.enums.Department;
import com.ip.api.domain.enums.UserStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import java.time.LocalDateTime;
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
    private String birth;
    private String userPhone;
    private LocalDateTime hireDate;
    private String jobTitle;
    private UserStatus userStatus;
    private Department department;
    @OneToOne
    @JoinColumn(name = "address_id", referencedColumnName = "addressId")
    private Address address;
}
