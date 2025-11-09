package com.co1119.kanban.dto.response;

import com.co1119.kanban.entity.User;

import lombok.Getter;

@Getter
public class RegisterResponse extends AbstractKanbanResponse {

    private final Long userId;
    private final String userEmail;

    public RegisterResponse(User registerUser) {
        this.userId = registerUser.getId();
        this.userEmail = registerUser.getEmail();
    }

}
