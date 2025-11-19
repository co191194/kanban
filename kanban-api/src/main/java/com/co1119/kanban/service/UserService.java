package com.co1119.kanban.service;

import com.co1119.kanban.dto.request.RegisterRequest;
import com.co1119.kanban.entity.User;

public interface UserService {

    /**
     * 新規ユーザを登録します。
     * 
     * @param request
     * @return
     */
    User registerUser(RegisterRequest request);

    /**
     * 現在のユーザ情報を取得します。
     * 
     * @return
     */
    User getCurrentUser();
}
