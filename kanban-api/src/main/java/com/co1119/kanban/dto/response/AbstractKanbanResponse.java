package com.co1119.kanban.dto.response;

import com.co1119.kanban.constant.ProcessResult;

import lombok.Data;

@Data
public abstract class AbstractKanbanResponse {
    private String processResult = ProcessResult.SUCCESS;
}
