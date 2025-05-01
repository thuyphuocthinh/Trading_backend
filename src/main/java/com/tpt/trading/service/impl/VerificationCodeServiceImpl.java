package com.tpt.trading.service.impl;

import com.tpt.trading.domain.VERIFICATION_TYPE;
import com.tpt.trading.entity.User;
import com.tpt.trading.entity.VerificationCode;
import com.tpt.trading.repository.VerificationCodeRepository;
import com.tpt.trading.service.VerificationCodeService;
import com.tpt.trading.utils.OtpUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VerificationCodeServiceImpl implements VerificationCodeService {
    private final VerificationCodeRepository verificationCodeRepository;

    @Override
    public void sendVerificationCode(User user, VERIFICATION_TYPE verificationType) {
        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setUser(user);
        verificationCode.setVerificationType(verificationType);
        verificationCode.setOtp(OtpUtils.generateOtp());
        verificationCodeRepository.save(verificationCode);
    }

    @Override
    public VerificationCode getVerificationCodeById(Long id) throws Exception {
        return verificationCodeRepository.findById(id)
                .orElseThrow(() -> new Exception("Verification code not found"));
    }

    @Override
    public VerificationCode getVerificationCodeByUser(Long userId) {
        return verificationCodeRepository.findByUserId(userId);
    }

    @Override
    public void deleteVerificationCodeById(Long id) {
        verificationCodeRepository.deleteById(id);
    }
}
