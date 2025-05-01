package com.tpt.trading.service;

import com.tpt.trading.entity.TwoFactorOTP;
import com.tpt.trading.entity.User;
import org.springframework.stereotype.Service;

public interface TwoFactorOtpService {
    TwoFactorOTP createTwoFactorOtp(User user, String otp, String jwt);
    TwoFactorOTP findByUser(Long userId);
    TwoFactorOTP findById(String id);
    boolean verifyTwoFactorOtp(TwoFactorOTP twoFactorOTP, String otp);
    void deleteTwoFactorOtp(String id);
}
