package com.ip.api.repository;

import com.ip.api.domain.PwResetCodes;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PwResetCodesRepository extends JpaRepository<PwResetCodes, Long> {
    PwResetCodes findByVerificationCode(String verificationCode);
}
