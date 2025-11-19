package com.co1119.kanban.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.co1119.kanban.entity.TaskList;

public interface TaskListRepository extends JpaRepository<TaskList, Long> {

}
