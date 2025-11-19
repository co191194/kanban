package com.co1119.kanban.dto.request;

import lombok.Data;

@Data
public class CardMoveRequest {
    private Long newTaskListId;
    private Double newOrderIndex;
}
