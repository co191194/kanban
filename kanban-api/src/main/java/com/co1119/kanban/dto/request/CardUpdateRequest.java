package com.co1119.kanban.dto.request;

import java.time.LocalDate;

import lombok.Data;

@Data
public class CardUpdateRequest {
    private String title;
    private String description;
    private LocalDate dueDate;
}
