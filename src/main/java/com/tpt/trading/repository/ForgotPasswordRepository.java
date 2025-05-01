package com.tpt.trading.repository;

import com.tpt.trading.entity.ForgotPasswordToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ForgotPasswordRepository extends JpaRepository<ForgotPasswordToken, Long> {
    ForgotPasswordToken findByUserId(Long userId);
    ForgotPasswordToken findByOtp(String otp);
}
