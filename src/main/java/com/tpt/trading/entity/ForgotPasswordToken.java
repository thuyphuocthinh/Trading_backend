package com.tpt.trading.entity;

import com.tpt.trading.domain.VERIFICATION_TYPE;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "forgot_password_tokens")
public class ForgotPasswordToken {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne
    private User user;

    private String otp;

    private VERIFICATION_TYPE verificationType;

    private String sendTo;
}
