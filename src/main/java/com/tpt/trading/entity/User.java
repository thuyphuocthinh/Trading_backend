package com.tpt.trading.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tpt.trading.domain.USER_ROLE;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;

    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    // @Embedded => still stored in 1 table => each property is a field
    @Embedded
    private TwoFactorAuth twoFactorAuth = new TwoFactorAuth();

    // by default => ROLE_CUSTOMER
    private USER_ROLE role = USER_ROLE.ROLE_CUSTOMER;
}
