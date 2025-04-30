package com.tpt.trading.controller;

import com.tpt.trading.config.JwtProvider;
import com.tpt.trading.dto.request.LoginRequest;
import com.tpt.trading.dto.response.AuthResponse;
import com.tpt.trading.entity.TwoFactorOTP;
import com.tpt.trading.entity.User;
import com.tpt.trading.repository.UserRepository;
import com.tpt.trading.service.CustomUserDetailService;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final UserRepository userRepository;

    private final CustomUserDetailService customUserDetailService;

    private final TwoFactorOtpService twoFactorOtpService;

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
            throw new BadCredentialsException("Invalid username or password");
        }
        if(!password.equals(userDetails.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}
