package com.tpt.trading.service.impl;

import com.tpt.trading.config.JwtProvider;
import com.tpt.trading.domain.VERIFICATION_TYPE;
import com.tpt.trading.entity.TwoFactorAuth;
import com.tpt.trading.entity.User;
import com.tpt.trading.repository.UserRepository;
import com.tpt.trading.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public User findUserProfileByJwt(String jwt) throws Exception {
        String email = JwtProvider.getEmailFromToken(jwt);
        User user = this.userRepository.findByEmail(email);
        if(user == null){
            throw new Exception("User not found");
        }
        return user;
    }

    @Override
    public User findUserByEmail(String email) throws Exception {
        User user = this.userRepository.findByEmail(email);
        if(user == null){
            throw new Exception("User not found");
        }
        return user;
    }

    @Override
    public User findUserById(Long id) throws Exception {
        return this.userRepository.findById(id)
                .orElseThrow(() -> new Exception("User not found"));
    }

    @Override
    public User enableTwoFactorAuthentication(VERIFICATION_TYPE verificationType, String sendTo, User user) {
        TwoFactorAuth twoFactorAuth = new TwoFactorAuth();
        twoFactorAuth.setEnabled(true);
        twoFactorAuth.setSendTo(verificationType);
        user.setTwoFactorAuth(twoFactorAuth);
        return this.userRepository.save(user);
    }

    @Override
    public User updatePassword(User user, String newPassword) {
        user.setPassword(newPassword);
        return userRepository.save(user);
    }
}
