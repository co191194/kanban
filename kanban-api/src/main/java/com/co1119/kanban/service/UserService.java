package com.co1119.kanban.service;

import com.co1119.kanban.dto.request.RegisterRequest;
import com.co1119.kanban.entity.User;

public interface UserService {

    User registerUser(RegisterRequest request);

}
