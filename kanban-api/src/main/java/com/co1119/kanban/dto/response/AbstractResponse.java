package com.co1119.kanban.dto.response;

import com.co1119.kanban.constant.ProcessResult;

import lombok.Data;

@Data
public abstract class AbstractResponse {
    private String processResult = ProcessResult.SUCCESS;
    private String message;
}
