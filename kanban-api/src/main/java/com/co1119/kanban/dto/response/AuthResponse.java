package com.co1119.kanban.dto.response;

import com.co1119.kanban.constant.SecurityConst;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class AuthResponse extends AbstractKanbanResponse {
    private final String accessToken;
    private final String tokenType = SecurityConst.TOKEN_TYPE;
}
