package com.co1119.kanban_api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.co1119.kanban.controller.TaskListController;
import com.co1119.kanban.dto.request.CardRequest;
import com.co1119.kanban.entity.Board;
import com.co1119.kanban.entity.Card;
import com.co1119.kanban.entity.TaskList;
import com.co1119.kanban.entity.User;
import com.co1119.kanban.service.TaskListService;
import com.fasterxml.jackson.databind.ObjectMapper;

class TaskListControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private TaskListService taskListService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        TaskListController controller = new TaskListController(taskListService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void createCard_success() throws Exception {
        // Given
        User user = new User("test@example.com", "password");
        user.setId(1L);

        Board board = new Board("Test Board", user);
        board.setId(1L);

        TaskList taskList = new TaskList("Test List", 0.0, board);
        taskList.setId(1L);

        Card newCard = new Card("New Card", 0.0, taskList);
        newCard.setId(1L);

        CardRequest request = new CardRequest();
        request.setTitle("New Card");

        when(taskListService.createCard(eq(1L), any(CardRequest.class))).thenReturn(newCard);

        // When & Then
        mockMvc.perform(post("/api/lists/1/cards")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.processResult").value("SUCCESS"))
                .andExpect(jsonPath("$.card.id").value(1))
                .andExpect(jsonPath("$.card.title").value("New Card"));
    }

    @Test
    void createCard_taskListNotFound() throws Exception {
        // Given
        CardRequest request = new CardRequest();
        request.setTitle("New Card");

        when(taskListService.createCard(eq(999L), any(CardRequest.class)))
                .thenThrow(new RuntimeException("タスクリストが見つかりません。"));

        // When & Then
        mockMvc.perform(post("/api/lists/999/cards")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.processResult").value("ERROR_NOT_FOUND"));
    }

    @Test
    void createCard_accessDenied() throws Exception {
        // Given
        CardRequest request = new CardRequest();
        request.setTitle("New Card");

        when(taskListService.createCard(eq(1L), any(CardRequest.class)))
                .thenThrow(new AccessDeniedException("タスクリストにアクセスする権限がありません。"));

        // When & Then
        mockMvc.perform(post("/api/lists/1/cards")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.processResult").value("ERROR_ACCESS_DNEY"));
    }
}
