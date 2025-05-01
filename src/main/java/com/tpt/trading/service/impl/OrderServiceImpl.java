package com.tpt.trading.service.impl;

import com.tpt.trading.domain.ORDER_STATUS;
import com.tpt.trading.domain.ORDER_TYPE;
import com.tpt.trading.entity.*;
import com.tpt.trading.repository.OrderItemRepository;
import com.tpt.trading.repository.OrderRepository;
import com.tpt.trading.service.AssetService;
import com.tpt.trading.service.OrderService;
import com.tpt.trading.service.WalletService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;

    private final OrderItemRepository orderItemRepository;

    private final WalletService walletService;

    private final AssetService assetService;

    @Override
    public Order getOrderById(Long orderId) throws Exception {
        return orderRepository.findById(orderId).orElseThrow(
                () -> new Exception("Order not found")
        );
    }

    @Override
    public Order createOrder(User user, OrderItem orderItem, ORDER_TYPE orderType) {
        double price = orderItem.getCoin().getCurrentPrice() * orderItem.getQuantity();
        Order order = new Order();
        order.setUser(user);
        order.setOrderType(orderType);
        order.setOrderItem(orderItem);
        order.setPrice(BigDecimal.valueOf(price));
        order.setTimestamp(LocalDateTime.now());
        order.setStatus(ORDER_STATUS.PENDING);
        return this.orderRepository.save(order);
    }

    @Override
    public List<Order> getAllOrdersByUser(Long userId, ORDER_TYPE orderType, String assetSymbol) {
        return orderRepository.findByUserId(userId);
    }

    private OrderItem createOrderItem(Coin coin, double quantity, double buyPrice, double sellPrice) {
        OrderItem orderItem = new OrderItem();
        orderItem.setCoin(coin);
        orderItem.setQuantity(quantity);
        orderItem.setBuyPrice(buyPrice);
        orderItem.setSellPrice(sellPrice);
        return this.orderItemRepository.save(orderItem);
    }

    @Transactional
    public Order buyAsset(Coin coin, double quantity, User user) throws Exception{
        if(quantity < 0) {
            throw new Exception("Quantity should be greater than 0");
        }
        double buyPrice = coin.getCurrentPrice();
        OrderItem orderItem = createOrderItem(coin, quantity, buyPrice, 0);
        Order order = createOrder(user, orderItem, ORDER_TYPE.BUY);
        orderItem.setOrder(order);
        orderItemRepository.save(orderItem);
        this.walletService.payOrderPayment(order, user);
        order.setStatus(ORDER_STATUS.SUCCESS);
        order.setOrderType(ORDER_TYPE.BUY);
        // create assets
        Asset asset = this.assetService.getAssetByUserIdAndId(user.getId(), order.getOrderItem().getId());
        if(asset == null) {
            this.assetService.createAsset(user, orderItem.getCoin(), orderItem.getQuantity());
        } else {
            this.assetService.updateAsset(asset.getId(), quantity);
        }
        return this.orderRepository.save(order);
    }

    @Transactional
    public Order sellAsset(Coin coin, double quantity, User user) throws Exception{
        if(quantity < 0) {
            throw new Exception("Quantity should be greater than 0");
        }
        double sellPrice = coin.getCurrentPrice();
        Asset assetToSell = this.assetService.findAssetByUserIdAndCoinId(user.getId(), coin.getId());
        if(assetToSell == null) {
            throw new Exception("Asset does not exist");
        }
        double buyPrice = assetToSell.getBuyPrice();
        OrderItem orderItem = createOrderItem(coin, quantity, buyPrice, sellPrice);
        Order order = createOrder(user, orderItem, ORDER_TYPE.SELL);
        orderItem.setOrder(order);
        if(assetToSell.getQuantity() >= quantity) {
            order.setStatus(ORDER_STATUS.SUCCESS);
            order.setOrderType(ORDER_TYPE.SELL);
            this.walletService.payOrderPayment(order, user);
            this.orderItemRepository.save(orderItem);
            Order savedOrder = this.orderRepository.save(order);
            Asset updatedAsset = assetService.updateAsset(assetToSell.getId(), -quantity);
            if(updatedAsset.getQuantity()*coin.getCurrentPrice() <= 1) {
                assetService.deleteAsset(updatedAsset.getId());
            }
            return savedOrder;
        }
        throw new Exception("Quantity should be greater than 0");

    }


    @Override
    @Transactional
    public Order processOrder(Coin coin, double quantity, ORDER_TYPE orderType, User user) throws Exception {
        if(orderType == ORDER_TYPE.BUY) {
            return buyAsset(coin, quantity, user);
        } else if (orderType == ORDER_TYPE.SELL) {
            return sellAsset(coin, quantity, user);
        }
        throw new Exception("Order type not supported");
    }
}
