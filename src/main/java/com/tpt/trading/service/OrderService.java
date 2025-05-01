package com.tpt.trading.service;

import com.tpt.trading.domain.ORDER_TYPE;
import com.tpt.trading.entity.Coin;
import com.tpt.trading.entity.Order;
import com.tpt.trading.entity.OrderItem;
import com.tpt.trading.entity.User;

import java.util.List;

public interface OrderService {
    Order getOrderById(Long orderId) throws Exception;
    Order createOrder(User user, OrderItem orderItem, ORDER_TYPE orderType);
    List<Order> getAllOrdersByUser(Long userId, ORDER_TYPE orderType, String assetSymbol);
    Order processOrder(Coin coin, double quantity, ORDER_TYPE orderType, User user) throws Exception;
}
