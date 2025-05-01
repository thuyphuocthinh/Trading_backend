package com.tpt.trading.controller;

import com.tpt.trading.config.JwtProvider;
import com.tpt.trading.dto.request.ForgotPasswordTokenRequest;
import com.tpt.trading.dto.request.LoginRequest;
import com.tpt.trading.dto.request.ResetPasswordRequest;
import com.tpt.trading.dto.response.AuthResponse;
import com.tpt.trading.entity.ForgotPasswordToken;
import com.tpt.trading.entity.TwoFactorOTP;
import com.tpt.trading.entity.User;
import com.tpt.trading.repository.UserRepository;
import com.tpt.trading.service.ForgotPasswordService;
import com.tpt.trading.service.UserService;
import com.tpt.trading.service.impl.CustomUserDetailService;
import com.tpt.trading.service.impl.EmailService;
import com.tpt.trading.service.TwoFactorOtpService;
import com.tpt.trading.utils.OtpUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final UserRepository userRepository;

    private final CustomUserDetailService customUserDetailService;

    private final TwoFactorOtpService twoFactorOtpService;

    private final EmailService emailService;

    private final ForgotPasswordService forgotPasswordService;

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody User user) throws Exception {
        User isUserExist = userRepository.findByEmail(user.getEmail());
        if (isUserExist != null) {
            throw new Exception("Email already exists");
        }

        User newUser = new User();
        newUser.setEmail(user.getEmail());
        newUser.setFullName(user.getFullName());
        newUser.setPassword(user.getPassword());
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getEmail(),
                user.getRole()
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = JwtProvider.generateToken(authentication);

        userRepository.save(newUser);
        AuthResponse authResponse = AuthResponse.builder()
                .jwt(jwt)
                .isTwoFactorEnabled(user.getTwoFactorAuth().isEnabled())
                .status(true)
                .message("Success")
                .build();
        return new ResponseEntity<>(authResponse, HttpStatus.CREATED);
    }

    @PostMapping("/log-in")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest) throws Exception {
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();
        Authentication authentication = authenticate(email, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = JwtProvider.generateToken(authentication);
        User user = userRepository.findByEmail(email);
        if(user.getTwoFactorAuth().isEnabled()) {
            String otp = OtpUtils.generateOtp();
            TwoFactorOTP oldTwoFactorOtp = this.twoFactorOtpService.findByUser(user.getId());
            if(oldTwoFactorOtp != null) {
                this.twoFactorOtpService.deleteTwoFactorOtp(oldTwoFactorOtp.getId());
            }
            TwoFactorOTP newTwoFactorOtp = this.twoFactorOtpService.createTwoFactorOtp(user, otp, jwt);
            AuthResponse authResponse = AuthResponse.builder()
                    .message("Two factor is enabled")
                    .isTwoFactorEnabled(true)
                    .status(true)
                    .session(newTwoFactorOtp.getId())
                    .build();
            emailService.sendVerificationOtpEmail(user.getEmail(), otp);
            return new ResponseEntity<>(authResponse, HttpStatus.ACCEPTED);
        }
        AuthResponse authResponse = AuthResponse.builder()
                .jwt(jwt)
                .status(true)
                .isTwoFactorEnabled(user.getTwoFactorAuth().isEnabled())
                .message("Success")
                .build();
        return new ResponseEntity<>(authResponse, HttpStatus.OK);
    }

    private Authentication authenticate(String username, String password) throws Exception {
        UserDetails userDetails = customUserDetailService.loadUserByUsername(username);
        if(userDetails == null) {
            throw new BadCredentialsException("Invalid username");
        }
        if(!password.equals(userDetails.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    @PostMapping("/verify-otp/{id}/{otp}")
    public ResponseEntity<AuthResponse> verifyLoginOtp(
            @PathVariable String otp, @PathVariable String id
    ) {
        TwoFactorOTP twoFactorOTP = twoFactorOtpService.findById(id);
        if(twoFactorOtpService.verifyTwoFactorOtp(twoFactorOTP, otp)) {
            AuthResponse authResponse = AuthResponse.builder()
                    .message("Two factor authentication verified")
                    .status(true)
                    .isTwoFactorEnabled(true)
                    .jwt(twoFactorOTP.getJwt())
                    .build();
            return new ResponseEntity<>(authResponse, HttpStatus.OK);
        }
        return null;
    }

    @PostMapping("/reset-password/send-otp")
    public ResponseEntity<String> sendForgotPasswordOtp(
            @RequestBody ForgotPasswordTokenRequest request
    ) throws Exception {
        User user = this.userService.findUserByEmail(request.getSendTo());
        String otp = OtpUtils.generateOtp();
        ForgotPasswordToken token = this.forgotPasswordService.findByUser(user.getId());
        if(token == null) {
            this.forgotPasswordService.createToken(user, otp, request.getVerificationType(), request.getSendTo());
        }
        this.emailService.sendVerificationOtpEmail(user.getEmail(), otp);
        return new ResponseEntity<>("OTP sent successfully", HttpStatus.OK);
    }

    @PostMapping("/reset-password/verify-otp/{otp}")
    public ResponseEntity<String> verifyForgotPasswordOtp(
            @PathVariable String otp
    ) throws Exception {
        ForgotPasswordToken token = this.forgotPasswordService.findByOtp(otp);
        if(token == null) {
            throw new Exception("Token invalid");
        }
        boolean isVerified = otp.equals(token.getOtp());
        if(!isVerified) {
            throw new Exception("Token invalid");
        }
        return new ResponseEntity<>("OTP is verified successfully", HttpStatus.OK);
    }

    @PatchMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @RequestBody ResetPasswordRequest request
            ) throws Exception {
        ForgotPasswordToken token = this.forgotPasswordService.findByOtp(request.getOtp());
        if(token == null) {
            throw new Exception("Token invalid");
        }
        User user = token.getUser();
        this.userService.updatePassword(user, request.getPassword());
        return new ResponseEntity<>("Updated successfully", HttpStatus.OK);
    }
}
