package com.tpt.trading.service;

import com.tpt.trading.entity.Order;
import com.tpt.trading.entity.User;
import com.tpt.trading.entity.Wallet;

import java.math.BigDecimal;

public interface WalletService {
    Wallet getUserWallet(User user);
    Wallet addBalance(Wallet wallet, BigDecimal amount);
    Wallet findById(Long id) throws Exception;
    Wallet walletToWalletTransfer(User sender, Wallet receiver, BigDecimal amount) throws Exception;
    Wallet payOrderPayment(Order order, User user) throws Exception;
}
