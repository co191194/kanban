package com.co1119.kanban_api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.co1119.kanban.controller.BoardController;
import com.co1119.kanban.dto.request.BoardRequest;
import com.co1119.kanban.entity.Board;
import com.co1119.kanban.entity.TaskList;
import com.co1119.kanban.entity.User;
import com.co1119.kanban.service.BoardService;
import com.fasterxml.jackson.databind.ObjectMapper;

class BoardControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private BoardService boardService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        BoardController controller = new BoardController(boardService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void getBoards_success() throws Exception {
        // Given
        User user = new User("test@example.com", "password");
        user.setId(1L);

        Board board1 = new Board("Board 1", user);
        board1.setId(1L);
        Board board2 = new Board("Board 2", user);
        board2.setId(2L);

        List<Board> boards = Arrays.asList(board1, board2);

        when(boardService.getBoardsForCurrentUser()).thenReturn(boards);

        // When & Then
        mockMvc.perform(get("/api/boards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.processResult").value("SUCCESS"))
                .andExpect(jsonPath("$.boards").isArray())
                .andExpect(jsonPath("$.boards.length()").value(2))
                .andExpect(jsonPath("$.boards[0].id").value(1))
                .andExpect(jsonPath("$.boards[0].title").value("Board 1"))
                .andExpect(jsonPath("$.boards[1].id").value(2))
                .andExpect(jsonPath("$.boards[1].title").value("Board 2"));
    }

    @Test
    void createBoard_success() throws Exception {
        // Given
        User user = new User("test@example.com", "password");
        user.setId(1L);

        Board newBoard = new Board("New Board", user);
        newBoard.setId(1L);

        BoardRequest request = new BoardRequest();
        request.setTitle("New Board");

        when(boardService.createBoard(any(BoardRequest.class))).thenReturn(newBoard);

        // When & Then
        mockMvc.perform(post("/api/boards")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.processResult").value("SUCCESS"))
                .andExpect(jsonPath("$.board.id").value(1))
                .andExpect(jsonPath("$.board.title").value("New Board"));
    }

    @Test
    void getBoardById_success() throws Exception {
        // Given
        User user = new User("test@example.com", "password");
        user.setId(1L);

        Board board = new Board("Test Board", user);
        board.setId(1L);

        when(boardService.getBoardById(1L)).thenReturn(board);

        // When & Then
        mockMvc.perform(get("/api/boards/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.processResult").value("SUCCESS"))
                .andExpect(jsonPath("$.board.id").value(1))
                .andExpect(jsonPath("$.board.title").value("Test Board"));
    }

    @Test
    void getBoardById_notFound() throws Exception {
        // Given
        when(boardService.getBoardById(999L)).thenThrow(new RuntimeException("ボードが見つかりません。"));

        // When & Then
        mockMvc.perform(get("/api/boards/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.processResult").value("ERROR_NOT_FOUND"));
    }

    @Test
    void getBoardById_accessDenied() throws Exception {
        // Given
        when(boardService.getBoardById(1L)).thenThrow(new AccessDeniedException("権限がありません。"));

        // When & Then
        mockMvc.perform(get("/api/boards/1"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.processResult").value("ERROR_ACCESS_DNEY"));
    }

    @Test
    void createList_success() throws Exception {
        // Given
        User user = new User("test@example.com", "password");
        user.setId(1L);

        Board board = new Board("Test Board", user);
        board.setId(1L);

        TaskList newList = new TaskList("New List", 0.0, board);
        newList.setId(1L);

        BoardRequest request = new BoardRequest();
        request.setTitle("New List");

        when(boardService.createList(eq(1L), any(BoardRequest.class))).thenReturn(newList);

        // When & Then
        mockMvc.perform(post("/api/boards/1/lists")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.processResult").value("SUCCESS"))
                .andExpect(jsonPath("$.taskList.id").value(1))
                .andExpect(jsonPath("$.taskList.title").value("New List"));
    }

    @Test
    void createList_boardNotFound() throws Exception {
        // Given
        BoardRequest request = new BoardRequest();
        request.setTitle("New List");

        when(boardService.createList(eq(999L), any(BoardRequest.class)))
                .thenThrow(new RuntimeException("ボードが見つかりません。"));

        // When & Then
        mockMvc.perform(post("/api/boards/999/lists")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.processResult").value("ERROR_NOT_FOUND"));
    }

    @Test
    void createList_accessDenied() throws Exception {
        // Given
        BoardRequest request = new BoardRequest();
        request.setTitle("New List");

        when(boardService.createList(eq(1L), any(BoardRequest.class)))
                .thenThrow(new AccessDeniedException("権限がありません。"));

        // When & Then
        mockMvc.perform(post("/api/boards/1/lists")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.processResult").value("ERROR_ACCESS_DNEY"));
    }
}
