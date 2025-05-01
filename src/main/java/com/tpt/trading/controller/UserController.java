package com.tpt.trading.controller;

import com.tpt.trading.config.JwtConstant;
import com.tpt.trading.domain.VERIFICATION_TYPE;
import com.tpt.trading.dto.request.ForgotPasswordTokenRequest;
import com.tpt.trading.entity.ForgotPasswordToken;
import com.tpt.trading.entity.User;
import com.tpt.trading.entity.VerificationCode;
import com.tpt.trading.service.UserService;
import com.tpt.trading.service.VerificationCodeService;
import com.tpt.trading.service.impl.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    private final EmailService emailService;

    private final VerificationCodeService verificationCodeService;

    @GetMapping("/profile")
    public ResponseEntity<User> getUserProfile(
            @RequestHeader(JwtConstant.JWT_TOKEN_HEADER) String jwt
    ) throws Exception {
        jwt = jwt.substring(7);
        User user = this.userService.findUserProfileByJwt(jwt);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping("/otp-verification/{verificationType}/send-otp")
    public ResponseEntity<String> sendVerificationOtp(
            @RequestHeader(JwtConstant.JWT_TOKEN_HEADER) String jwt,
            @PathVariable String verificationType
    ) throws Exception {
        User user = this.userService.findUserProfileByJwt(jwt.substring(7));
        VerificationCode verificationCode = this.verificationCodeService.getVerificationCodeByUser(user.getId());
        if(verificationCode == null) {
            this.verificationCodeService.sendVerificationCode(user, VERIFICATION_TYPE.valueOf(verificationType));
        }
        if(verificationType.equals(VERIFICATION_TYPE.EMAIL.toString())) {
            assert verificationCode != null;
            emailService.sendVerificationOtpEmail(user.getEmail(), verificationCode.getOtp());
        }
        return new ResponseEntity<>("Verification otp sent successfully", HttpStatus.OK);
    }

    @PatchMapping("/otp-verification/verify-otp/{otp}")
    public ResponseEntity<User> verifyOtp(
            @RequestHeader(JwtConstant.JWT_TOKEN_HEADER) String jwt,
            @PathVariable String otp
    ) throws Exception {
        User user = this.userService.findUserProfileByJwt(jwt.substring(7));
        VerificationCode verificationCode = this.verificationCodeService.getVerificationCodeByUser(user.getId());
        String sendTo = verificationCode.getVerificationType().equals(VERIFICATION_TYPE.EMAIL) ?
                verificationCode.getEmail() : verificationCode.getMobile();
        boolean isVerified = verificationCode.getOtp().equals(otp);
        if(isVerified) {
            User updatedUser = this.userService.enableTwoFactorAuthentication(
                    verificationCode.getVerificationType(),
                    sendTo,
                    user
            );
            this.verificationCodeService.deleteVerificationCodeById(verificationCode.getId());
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        }
        throw new Exception("OTP verification failed");
    }

}
