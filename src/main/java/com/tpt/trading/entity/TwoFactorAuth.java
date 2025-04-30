package com.tpt.trading.entity;

import com.tpt.trading.domain.VERIFICATION_TYPE;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TwoFactorAuth {
    private boolean isEnabled = false;
    private VERIFICATION_TYPE sendTo;
}
