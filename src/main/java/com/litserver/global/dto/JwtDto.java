package com.litserver.global.dto;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class JwtDto {
    private String token;
}