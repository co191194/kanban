package com.co1119.kanban.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.co1119.kanban.entity.Board;

public interface BoardRepository extends JpaRepository<Board, Long> {

    /**
     * ユーザIDでボードを検索します。
     * 
     * @param userId
     * @return
     */
    List<Board> findByUserId(Long userId);
}
