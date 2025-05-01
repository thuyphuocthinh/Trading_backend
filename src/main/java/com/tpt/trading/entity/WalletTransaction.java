package com.tpt.trading.entity;

import com.tpt.trading.domain.WALLET_TRANSACTION_TYPE;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "wallet_transactions")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WalletTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private Wallet wallet;

    private WALLET_TRANSACTION_TYPE type;

    private LocalDate date;

    private String transferId;

    private String purpose;

    private Long amount;
}
