package com.tpt.trading.controller;

import com.tpt.trading.domain.ORDER_TYPE;
import com.tpt.trading.dto.request.CreateOrderRequest;
import com.tpt.trading.entity.Coin;
import com.tpt.trading.entity.Order;
import com.tpt.trading.entity.User;
import com.tpt.trading.service.CoinService;
import com.tpt.trading.service.OrderService;
import com.tpt.trading.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    private final UserService userService;

    private final CoinService coinService;

    @PostMapping("/pay")
    public ResponseEntity<Order> payOrder(
            @RequestHeader("Authorization") String jwt,
            @RequestBody CreateOrderRequest request
    ) throws Exception {
        User user = this.userService.findUserProfileByJwt(jwt.substring(7));
        Coin coin = this.coinService.findById(request.getCoinId());
        Order order = this.orderService.processOrder(coin, request.getQuantity(), request.getOrderType(), user);
        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrder(
            @RequestHeader("Authorization") String jwt,
            @PathVariable Long orderId
    ) throws Exception {
        User user = this.userService.findUserProfileByJwt(jwt.substring(7));
        Order order = this.orderService.getOrderById(orderId);
        if(!order.getUser().getId().equals(user.getId())) {
            throw new Exception("You don't have access");
        } else {
            return new ResponseEntity<>(order, HttpStatus.OK);
        }
    }

    @GetMapping("/get-all-by-users")
    public ResponseEntity<List<Order>> getAllOrdersByUser(
            @RequestHeader("Authorization") String jwt,
            @RequestParam(required = false) ORDER_TYPE order_type,
            @RequestParam(required = false) String asset_symbol
    ) throws Exception {
        Long userId = this.userService.findUserProfileByJwt(jwt.substring(7)).getId();
        List<Order> userOrders = this.orderService.getAllOrdersByUser(userId, order_type, asset_symbol);
        return new ResponseEntity<>(userOrders, HttpStatus.OK);
    }
}
