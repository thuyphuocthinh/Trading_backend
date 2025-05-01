package com.tpt.trading.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class JwtConstant {
    public static final String JWT_TOKEN_HEADER = "Authorization";
    public static final String SECRET_KEY = "thuyphuocthinh012498765thinhphuocthuy";
    public static final String TOKEN_PREFIX = "Bearer ";
}
