package com.co1119.kanban_api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import com.co1119.kanban.dto.request.BoardRequest;
import com.co1119.kanban.entity.Board;
import com.co1119.kanban.entity.TaskList;
import com.co1119.kanban.entity.User;
import com.co1119.kanban.repository.BoardRepository;
import com.co1119.kanban.repository.TaskListRepository;
import com.co1119.kanban.service.BoardServiceImpl;
import com.co1119.kanban.service.UserService;

@ExtendWith(MockitoExtension.class)
class BoardServiceImplTest {

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private TaskListRepository taskListRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private BoardServiceImpl boardService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User("test@example.com", "password");
        testUser.setId(1L);
    }

    @Test
    void getBoardsForCurrentUser_success() {
        // Given
        Board board1 = new Board("Board 1", testUser);
        board1.setId(1L);
        Board board2 = new Board("Board 2", testUser);
        board2.setId(2L);
        List<Board> expectedBoards = Arrays.asList(board1, board2);

        when(userService.getCurrentUser()).thenReturn(testUser);
        when(boardRepository.findByUserId(1L)).thenReturn(expectedBoards);

        // When
        List<Board> actualBoards = boardService.getBoardsForCurrentUser();

        // Then
        assertNotNull(actualBoards);
        assertEquals(2, actualBoards.size());
        assertEquals("Board 1", actualBoards.get(0).getTitle());
        assertEquals("Board 2", actualBoards.get(1).getTitle());
        verify(userService).getCurrentUser();
        verify(boardRepository).findByUserId(1L);
    }

    @Test
    void createBoard_success() {
        // Given
        BoardRequest request = new BoardRequest();
        request.setTitle("New Board");

        Board savedBoard = new Board("New Board", testUser);
        savedBoard.setId(1L);

        when(userService.getCurrentUser()).thenReturn(testUser);
        when(boardRepository.save(any(Board.class))).thenReturn(savedBoard);

        // When
        Board result = boardService.createBoard(request);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("New Board", result.getTitle());
        assertEquals(testUser, result.getUser());
        verify(userService).getCurrentUser();
        verify(boardRepository).save(any(Board.class));
    }

    @Test
    void getBoardById_success() {
        // Given
        Board board = new Board("Test Board", testUser);
        board.setId(1L);

        when(userService.getCurrentUser()).thenReturn(testUser);
        when(boardRepository.findById(1L)).thenReturn(Optional.of(board));

        // When
        Board result = boardService.getBoardById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Board", result.getTitle());
        verify(userService).getCurrentUser();
        verify(boardRepository).findById(1L);
    }

    @Test
    void getBoardById_notFound() {
        // Given
        when(userService.getCurrentUser()).thenReturn(testUser);
        when(boardRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            boardService.getBoardById(999L);
        });

        assertEquals("ボードが見つかりません。", exception.getMessage());
        verify(userService).getCurrentUser();
        verify(boardRepository).findById(999L);
    }

    @Test
    void getBoardById_accessDenied() {
        // Given
        User anotherUser = new User("another@example.com", "password");
        anotherUser.setId(2L);

        Board board = new Board("Another's Board", anotherUser);
        board.setId(1L);

        when(userService.getCurrentUser()).thenReturn(testUser);
        when(boardRepository.findById(1L)).thenReturn(Optional.of(board));

        // When & Then
        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> {
            boardService.getBoardById(1L);
        });

        assertEquals("権限がありません。", exception.getMessage());
        verify(userService).getCurrentUser();
        verify(boardRepository).findById(1L);
    }

    @Test
    void createList_success() {
        // Given
        Board board = new Board("Test Board", testUser);
        board.setId(1L);

        BoardRequest request = new BoardRequest();
        request.setTitle("New List");

        TaskList savedList = new TaskList("New List", 0.0, board);
        savedList.setId(1L);

        when(userService.getCurrentUser()).thenReturn(testUser);
        when(boardRepository.findById(1L)).thenReturn(Optional.of(board));
        when(taskListRepository.save(any(TaskList.class))).thenReturn(savedList);

        // When
        TaskList result = boardService.createList(1L, request);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("New List", result.getTitle());
        assertEquals(0, result.getOrderIndex());
        assertEquals(board, result.getBoard());
        verify(userService).getCurrentUser();
        verify(boardRepository).findById(1L);
        verify(taskListRepository).save(any(TaskList.class));
    }

    @Test
    void createList_boardNotFound() {
        // Given
        BoardRequest request = new BoardRequest();
        request.setTitle("New List");

        when(userService.getCurrentUser()).thenReturn(testUser);
        when(boardRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            boardService.createList(999L, request);
        });

        assertEquals("ボードが見つかりません。", exception.getMessage());
        verify(userService).getCurrentUser();
        verify(boardRepository).findById(999L);
    }

    @Test
    void createList_accessDenied() {
        // Given
        User anotherUser = new User("another@example.com", "password");
        anotherUser.setId(2L);

        Board board = new Board("Another's Board", anotherUser);
        board.setId(1L);

        BoardRequest request = new BoardRequest();
        request.setTitle("New List");

        when(userService.getCurrentUser()).thenReturn(testUser);
        when(boardRepository.findById(1L)).thenReturn(Optional.of(board));

        // When & Then
        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> {
            boardService.createList(1L, request);
        });

        assertEquals("権限がありません。", exception.getMessage());
        verify(userService).getCurrentUser();
        verify(boardRepository).findById(1L);
    }
}
