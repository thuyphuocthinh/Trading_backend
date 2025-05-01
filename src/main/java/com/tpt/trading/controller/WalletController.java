package com.tpt.trading.controller;

import com.tpt.trading.entity.Order;
import com.tpt.trading.entity.User;
import com.tpt.trading.entity.Wallet;
import com.tpt.trading.entity.WalletTransaction;
import com.tpt.trading.service.OrderService;
import com.tpt.trading.service.UserService;
import com.tpt.trading.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/wallets")
@RequiredArgsConstructor
public class WalletController {
    private final WalletService walletService;

    private final UserService userService;

    private final OrderService orderService;

    @GetMapping("/get-by-user")
    public ResponseEntity<Wallet> getUserWallet(
            @RequestHeader("Authorization") String jwt
    ) throws Exception {
        User user = this.userService.findUserProfileByJwt(jwt.substring(7));
        Wallet wallet = this.walletService.getUserWallet(user);
        return new ResponseEntity<>(wallet, HttpStatus.ACCEPTED);
    }

    @PutMapping("/transfer-to/{walletId}")
    public ResponseEntity<Wallet> walletToWallet(
            @RequestHeader("Authorization") String jwt,
            @PathVariable Long walletId,
            @RequestBody WalletTransaction request
    ) throws Exception {
        User senderUser = this.userService.findUserProfileByJwt(jwt.substring(7));
        Wallet receiverWallet = this.walletService.findById(walletId);
        Wallet wallet = this.walletService.walletToWalletTransfer(senderUser, receiverWallet, BigDecimal.valueOf(request.getAmount()));
        return new ResponseEntity<>(wallet, HttpStatus.ACCEPTED);
    }

    @PutMapping("/payment/order/{orderId}")
    public ResponseEntity<Wallet> payOrderWithWallet(
            @RequestHeader("Authorization") String jwt,
            @PathVariable Long orderId
    ) throws Exception {
        User user = this.userService.findUserProfileByJwt(jwt.substring(7));
        Order order = this.orderService.getOrderById(orderId);
        Wallet wallet = this.walletService.payOrderPayment(order, user);
        return new ResponseEntity<>(wallet, HttpStatus.ACCEPTED);
    }
}
