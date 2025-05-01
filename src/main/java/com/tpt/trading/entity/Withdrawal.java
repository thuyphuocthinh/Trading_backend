package com.tpt.trading.entity;

import com.tpt.trading.domain.WITHDRAWAL_STATUS;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "withdrawals")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Withdrawal {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private WITHDRAWAL_STATUS withdrawalStatus;

    private Long amount;

    @ManyToOne(fetch = FetchType.EAGER)
    private User user;

    private LocalDateTime date = LocalDateTime.now();
}
