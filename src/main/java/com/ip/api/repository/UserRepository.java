package com.ip.api.repository;

import com.ip.api.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByConnId(String connId);
    boolean existsByEmail(String email);
}