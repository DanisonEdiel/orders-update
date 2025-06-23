package com.example.orderupdate.security;

import lombok.Data;

import java.util.List;

@Data
public class TokenValidationResponse {
    private boolean valid;
    private String userId;
    private String email;
    private List<String> roles;
    private String error;
}
