package com.tpt.trading.service;

import com.tpt.trading.domain.VERIFICATION_TYPE;
import com.tpt.trading.entity.ForgotPasswordToken;
import com.tpt.trading.entity.User;

public interface ForgotPasswordService {
    void createToken(User user, String otp, VERIFICATION_TYPE verificationType, String sendTo);
    ForgotPasswordToken findById(Long id) throws Exception;
    ForgotPasswordToken findByUser(Long userId);
    void deleteToken(Long id);
    ForgotPasswordToken findByOtp(String otp);
}
