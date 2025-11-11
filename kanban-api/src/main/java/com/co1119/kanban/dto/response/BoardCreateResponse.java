package com.co1119.kanban.dto.response;

import com.co1119.kanban.entity.Board;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class BoardCreateResponse extends AbstractKanbanResponse {
    private final Board board;
}
