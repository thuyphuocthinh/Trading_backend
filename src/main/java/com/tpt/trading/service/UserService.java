package com.tpt.trading.service;

import com.tpt.trading.domain.VERIFICATION_TYPE;
import com.tpt.trading.entity.User;

public interface UserService {
    public User findUserProfileByJwt(String jwt) throws Exception;
    public User findUserByEmail(String email) throws Exception;
    public User findUserById(Long id) throws Exception;
    public User enableTwoFactorAuthentication(VERIFICATION_TYPE verificationType, String sendTo, User user);
    public User updatePassword(User user, String newPassword);
}
