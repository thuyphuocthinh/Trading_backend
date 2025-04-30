package com.tpt.trading.service;

import com.tpt.trading.entity.TwoFactorOTP;
import com.tpt.trading.entity.User;
import com.tpt.trading.repository.TwoFactorOtpRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TwoFactorOtpServiceImpl implements TwoFactorOtpService {
    private final TwoFactorOtpRepository twoFactorOtpRepository;

    @Override
    public TwoFactorOTP createTwoFactorOtp(User user, String otp, String jwt) {
        UUID uuid = UUID.randomUUID();
        String id = uuid.toString();
        TwoFactorOTP twoFactorOTP = TwoFactorOTP
                .builder()
                .id(id)
                .otp(otp)
                .jwt(jwt)
                .user(user)
                .build();
        return twoFactorOtpRepository.save(twoFactorOTP);
    }

    @Override
    public TwoFactorOTP findByUser(Long userId) {
        return twoFactorOtpRepository.findByUserId(userId);
    }

    @Override
    public TwoFactorOTP findById(String id) {
        Optional<TwoFactorOTP> twoFactorOtp = twoFactorOtpRepository.findById(id);
        return twoFactorOtp.orElse(null);
    }

    @Override
    public boolean verifyTwoFactorOtp(TwoFactorOTP twoFactorOTP, String otp) {
        return twoFactorOTP.getOtp().equals(otp);
    }

    @Override
    public void deleteTwoFactorOtp(String id) {
        TwoFactorOTP twoFactorOTP = twoFactorOtpRepository.findById(id).orElse(null);
        assert twoFactorOTP != null;
        this.twoFactorOtpRepository.delete(twoFactorOTP);
    }
}
