package com.tpt.trading.service.impl;

import com.tpt.trading.domain.WITHDRAWAL_STATUS;
import com.tpt.trading.entity.User;
import com.tpt.trading.entity.Withdrawal;
import com.tpt.trading.repository.WithdrawalRepository;
import com.tpt.trading.service.WithdrawalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WithdrawalServiceImpl implements WithdrawalService {
    private final WithdrawalRepository withdrawalRepository;

    @Override
    public Withdrawal requestWithdrawal(Long amount, User user) {
        Withdrawal withdrawal = new Withdrawal();
        withdrawal.setAmount(amount);
        withdrawal.setUser(user);
        withdrawal.setWithdrawalStatus(WITHDRAWAL_STATUS.PENDING);
        return this.withdrawalRepository.save(withdrawal);
    }

    @Override
    public Withdrawal processWithdrawal(Long withdrawalId, boolean accept) throws Exception {
        Withdrawal withdrawal = this.withdrawalRepository.findById(withdrawalId).orElse(null);
        if(withdrawal == null){
            throw new Exception("Withdrawal not found");
        }
        withdrawal.setDate(LocalDateTime.now());
        if(accept){
            withdrawal.setWithdrawalStatus(WITHDRAWAL_STATUS.SUCCESS);
        } else {
            withdrawal.setWithdrawalStatus(WITHDRAWAL_STATUS.DECLINE);
        }
        return this.withdrawalRepository.save(withdrawal);
    }

    @Override
    public List<Withdrawal> getUserWithdrawalsHistory(Long userId) {
        return this.withdrawalRepository.findByUserId(userId);
    }

    @Override
    public List<Withdrawal> getAllWithdrawalRequest() {
        return this.withdrawalRepository.findAll();
    }
}
