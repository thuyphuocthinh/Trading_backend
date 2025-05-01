package com.tpt.trading.dto.request;

import com.tpt.trading.domain.VERIFICATION_TYPE;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ForgotPasswordTokenRequest {
    private String sendTo;
    private VERIFICATION_TYPE verificationType;
}
