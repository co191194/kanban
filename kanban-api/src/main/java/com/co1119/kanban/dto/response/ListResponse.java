package com.co1119.kanban.dto.response;

import com.co1119.kanban.entity.TaskList;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ListResponse extends AbstractResponse {

    private final TaskList taskList;

    public static ListResponse createTaskListResponse(TaskList taskList) {
        return new ListResponse(taskList);
    }
}
