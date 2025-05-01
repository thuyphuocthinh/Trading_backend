package com.tpt.trading.service;

import com.tpt.trading.entity.User;
import com.tpt.trading.entity.Withdrawal;

import java.util.List;

public interface WithdrawalService {
    Withdrawal requestWithdrawal(Long amount, User user);
    Withdrawal processWithdrawal(Long withdrawalId, boolean accept) throws Exception;
    List<Withdrawal> getUserWithdrawalsHistory(Long userId);
    List<Withdrawal> getAllWithdrawalRequest();
}
