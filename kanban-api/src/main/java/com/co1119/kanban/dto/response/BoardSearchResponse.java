package com.co1119.kanban.dto.response;

import java.util.List;

import com.co1119.kanban.entity.Board;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class BoardSearchResponse extends AbstractKanbanResponse {
    private final List<Board> boards;
}
