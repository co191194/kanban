package com.co1119.kanban_api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import com.co1119.kanban.dto.request.CardRequest;
import com.co1119.kanban.entity.Board;
import com.co1119.kanban.entity.Card;
import com.co1119.kanban.entity.TaskList;
import com.co1119.kanban.entity.User;
import com.co1119.kanban.repository.CardRepository;
import com.co1119.kanban.repository.TaskListRepository;
import com.co1119.kanban.service.TaskListServiceImpl;
import com.co1119.kanban.service.UserService;

@ExtendWith(MockitoExtension.class)
class TaskListServiceImplTest {

    @Mock
    private TaskListRepository taskListRepository;

    @Mock
    private CardRepository cardRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private TaskListServiceImpl taskListService;

    private User testUser;
    private Board testBoard;
    private TaskList testTaskList;

    @BeforeEach
    void setUp() {
        testUser = new User("test@example.com", "password");
        testUser.setId(1L);

        testBoard = new Board("Test Board", testUser);
        testBoard.setId(1L);

        testTaskList = new TaskList("Test List", 0.0, testBoard);
        testTaskList.setId(1L);
    }

    @Test
    void createCard_success() {
        // Given
        CardRequest request = new CardRequest();
        request.setTitle("New Card");

        Card savedCard = new Card("New Card", 0.0, testTaskList);
        savedCard.setId(1L);

        when(userService.getCurrentUser()).thenReturn(testUser);
        when(taskListRepository.findById(1L)).thenReturn(Optional.of(testTaskList));
        when(cardRepository.save(any(Card.class))).thenReturn(savedCard);

        // When
        Card result = taskListService.createCard(1L, request);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("New Card", result.getTitle());
        assertEquals(0, result.getOrderIndex());
        assertEquals(testTaskList, result.getTaskList());
        verify(userService).getCurrentUser();
        verify(taskListRepository).findById(1L);
        verify(cardRepository).save(any(Card.class));
    }

    @Test
    void createCard_taskListNotFound() {
        // Given
        CardRequest request = new CardRequest();
        request.setTitle("New Card");

        when(taskListRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            taskListService.createCard(999L, request);
        });

        assertEquals("タスクリストが見つかりません。", exception.getMessage());
        verify(taskListRepository).findById(999L);
    }

    @Test
    void createCard_accessDenied() {
        // Given
        User anotherUser = new User("another@example.com", "password");
        anotherUser.setId(2L);

        Board anotherBoard = new Board("Another's Board", anotherUser);
        anotherBoard.setId(2L);

        TaskList anotherTaskList = new TaskList("Another's List", 0.0, anotherBoard);
        anotherTaskList.setId(2L);

        CardRequest request = new CardRequest();
        request.setTitle("New Card");

        when(userService.getCurrentUser()).thenReturn(testUser);
        when(taskListRepository.findById(2L)).thenReturn(Optional.of(anotherTaskList));

        // When & Then
        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> {
            taskListService.createCard(2L, request);
        });

        assertEquals("タスクリストにアクセスする権限がありません。", exception.getMessage());
        verify(userService).getCurrentUser();
        verify(taskListRepository).findById(2L);
    }

    @Test
    void createCard_withMultipleExistingCards() {
        // Given
        Card existingCard1 = new Card("Card 1", 0.0, testTaskList);
        existingCard1.setId(1L);
        Card existingCard2 = new Card("Card 2", 1.0, testTaskList);
        existingCard2.setId(2L);

        testTaskList.getCards().add(existingCard1);
        testTaskList.getCards().add(existingCard2);

        CardRequest request = new CardRequest();
        request.setTitle("Card 3");

        Card savedCard = new Card("Card 3", 2.0, testTaskList);
        savedCard.setId(3L);

        when(userService.getCurrentUser()).thenReturn(testUser);
        when(taskListRepository.findById(1L)).thenReturn(Optional.of(testTaskList));
        when(cardRepository.save(any(Card.class))).thenReturn(savedCard);

        // When
        Card result = taskListService.createCard(1L, request);

        // Then
        assertNotNull(result);
        assertEquals(3L, result.getId());
        assertEquals("Card 3", result.getTitle());
        assertEquals(2, result.getOrderIndex()); // 既存のカードが2枚あるので、次のインデックスは2
        verify(userService).getCurrentUser();
        verify(taskListRepository).findById(1L);
        verify(cardRepository).save(any(Card.class));
    }
}
