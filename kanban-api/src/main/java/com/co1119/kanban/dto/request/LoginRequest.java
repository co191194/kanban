package com.co1119.kanban.dto.request;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}
