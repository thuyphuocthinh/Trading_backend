package com.tpt.trading.service.impl;

import com.tpt.trading.domain.VERIFICATION_TYPE;
import com.tpt.trading.entity.ForgotPasswordToken;
import com.tpt.trading.entity.User;
import com.tpt.trading.repository.ForgotPasswordRepository;
import com.tpt.trading.service.ForgotPasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ForgotPasswordServiceImpl implements ForgotPasswordService {
    private final ForgotPasswordRepository forgotPasswordRepository;

    @Override
    public void createToken(User user, String otp, VERIFICATION_TYPE verificationType, String sendTo) {
        ForgotPasswordToken token = new ForgotPasswordToken();
        token.setUser(user);
        token.setOtp(otp);
        token.setVerificationType(verificationType);
        token.setSendTo(sendTo);
        this.forgotPasswordRepository.save(token);
    }

    @Override
    public ForgotPasswordToken findById(Long id) throws Exception {
        return this.forgotPasswordRepository.findById(id)
                .orElseThrow(() -> new Exception("Forgot Password Token Not Found"));
    }

    @Override
    public ForgotPasswordToken findByUser(Long userId) {
        return this.forgotPasswordRepository.findByUserId(userId);
    }

    @Override
    public void deleteToken(Long id) {
        this.forgotPasswordRepository.deleteById(id);
    }

    @Override
    public ForgotPasswordToken findByOtp(String otp) {
        return this.forgotPasswordRepository.findByOtp(otp);
    }
}
