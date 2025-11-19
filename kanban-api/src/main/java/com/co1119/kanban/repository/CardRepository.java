package com.co1119.kanban.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.co1119.kanban.entity.Card;

public interface CardRepository extends JpaRepository<Card, Long> {

}
