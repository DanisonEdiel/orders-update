package com.example.orderupdate.security;

import lombok.Data;

import java.util.List;

@Data
public class UserPrincipal {
    private String userId;
    private String email;
    private List<String> roles;
}
