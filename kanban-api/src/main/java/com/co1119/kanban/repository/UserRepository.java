package com.co1119.kanban.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.co1119.kanban.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * メールアドレスでユーザを検索します。
     *
     * @param email
     * @return
     */
    Optional<User> findByEmail(String email);
}
