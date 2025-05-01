package com.tpt.trading.service;

import com.tpt.trading.domain.VERIFICATION_TYPE;
import com.tpt.trading.entity.User;
import com.tpt.trading.entity.VerificationCode;

public interface VerificationCodeService {
    public void sendVerificationCode(User user, VERIFICATION_TYPE verificationType);
    public VerificationCode getVerificationCodeById(Long id) throws Exception;
    public VerificationCode getVerificationCodeByUser(Long userId);
    void deleteVerificationCodeById(Long id);
}
