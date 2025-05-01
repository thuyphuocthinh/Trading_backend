package com.tpt.trading.dto.request;

import com.tpt.trading.domain.ORDER_TYPE;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderRequest {
    private String coinId;
    private double quantity;
    private ORDER_TYPE orderType;
}
