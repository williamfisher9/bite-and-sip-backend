package com.apps.biteandsip.dao;

import com.apps.biteandsip.model.PasswordToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordTokenRepository extends JpaRepository<PasswordToken, Long> {
    Optional<PasswordToken> findByToken(String token);
}
