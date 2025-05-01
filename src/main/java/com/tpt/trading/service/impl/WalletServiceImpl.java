package com.tpt.trading.service.impl;

import com.tpt.trading.domain.ORDER_TYPE;
import com.tpt.trading.entity.Order;
import com.tpt.trading.entity.User;
import com.tpt.trading.entity.Wallet;
import com.tpt.trading.repository.WalletRepository;
import com.tpt.trading.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {
    private final WalletRepository walletRepository;

    @Override
    public Wallet getUserWallet(User user) {
        Wallet wallet = walletRepository.findByUserId(user.getId());
        if (wallet == null) {
            Wallet newWallet = new Wallet();
            newWallet.setUser(user);
            newWallet.setBalance(BigDecimal.ZERO);
            wallet = walletRepository.save(newWallet);
        }
        return wallet;
    }

    @Override
    public Wallet addBalance(Wallet wallet, BigDecimal amount) {
        BigDecimal balance = wallet.getBalance();
        BigDecimal newBalance = balance.add(amount);
        wallet.setBalance(newBalance);
        return walletRepository.save(wallet);
    }

    @Override
    public Wallet findById(Long id) throws Exception {
        return this.walletRepository.findById(id).orElseThrow(
                () -> new Exception("Wallet not found")
        );
    }

    @Override
    public Wallet walletToWalletTransfer(User sender, Wallet receiver, BigDecimal amount) throws Exception {
        Wallet senderWallet = this.getUserWallet(sender);
        if(senderWallet.getBalance().compareTo(amount) < 0) {
            throw new Exception("Insufficient balance");
        }
        BigDecimal senderWalletBalance = senderWallet.getBalance().subtract(amount);
        BigDecimal receiverWalletBalance = receiver.getBalance().add(amount);
        senderWallet.setBalance(senderWalletBalance);
        receiver.setBalance(receiverWalletBalance);
        walletRepository.save(senderWallet);
        walletRepository.save(receiver);
        return senderWallet;
    }

    @Override
    public Wallet payOrderPayment(Order order, User user) throws Exception {
        Wallet wallet = this.getUserWallet(user);
        if(order.getOrderType().equals(ORDER_TYPE.BUY)) {
            BigDecimal newBalance = wallet.getBalance().subtract(order.getPrice());
            if(newBalance.compareTo(BigDecimal.ZERO) < 0) {
                throw new Exception("Insufficient balance");
            }
            wallet.setBalance(newBalance);
            wallet = walletRepository.save(wallet);
        }
        if(order.getOrderType().equals(ORDER_TYPE.SELL)) {
            BigDecimal newBalance = wallet.getBalance().add(order.getPrice());
            wallet.setBalance(newBalance);
            wallet = walletRepository.save(wallet);
        }
        return wallet;
    }
}
