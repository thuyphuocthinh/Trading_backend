package com.tpt.trading.controller;

import com.tpt.trading.entity.User;
import com.tpt.trading.entity.Wallet;
import com.tpt.trading.entity.Withdrawal;
import com.tpt.trading.service.UserService;
import com.tpt.trading.service.WalletService;
import com.tpt.trading.service.WithdrawalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/withdrawals")
@RequiredArgsConstructor
public class WithdrawalController {
    private final WithdrawalService withdrawalService;

    private final WalletService walletService;

    private final UserService userService;

    @PostMapping("/{amount}")
    public ResponseEntity<Withdrawal> createWithdrawal(
            @PathVariable Long amount,
            @RequestHeader("Authorization") String jwt
    ) throws Exception {
        User user = this.userService.findUserProfileByJwt(jwt.substring(7));
        Wallet userWallet = this.walletService.getUserWallet(user);

        Withdrawal withdrawal = this.withdrawalService.requestWithdrawal(amount, user);
        walletService.addBalance(userWallet, BigDecimal.valueOf(-withdrawal.getAmount()));
        return new ResponseEntity<>(withdrawal, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/process-withdrawal/{accept}")
    public ResponseEntity<Withdrawal> processWithdrawal(
            @PathVariable Long id,
            @PathVariable boolean accept,
            @RequestHeader("Authorization") String jwt
    ) throws Exception {
        User user = this.userService.findUserProfileByJwt(jwt.substring(7));
        Withdrawal withdrawal = this.withdrawalService.processWithdrawal(id, accept);
        Wallet userWallet = this.walletService.getUserWallet(user);
        if(!accept) {
            walletService.addBalance(userWallet, BigDecimal.valueOf(withdrawal.getAmount()));
        }
        return new ResponseEntity<>(withdrawal, HttpStatus.OK);
    }

    @GetMapping("/get-by-users")
    public ResponseEntity<List<Withdrawal>> getAllWithdrawalRequestByUser(
            @RequestHeader("Authorization") String jwt
    ) throws Exception {
        User user = this.userService.findUserProfileByJwt(jwt.substring(7));
        return new ResponseEntity<>(
                this.withdrawalService.getUserWithdrawalsHistory(user.getId()),
                HttpStatus.ACCEPTED
        );
    }

    @GetMapping("/get-by-admin")
    public ResponseEntity<List<Withdrawal>> getAllWithdrawalRequestByAdmin() throws Exception {
        return new ResponseEntity<>(
                this.withdrawalService.getAllWithdrawalRequest(),
                HttpStatus.ACCEPTED
        );
    }
}
