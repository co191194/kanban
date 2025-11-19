package com.co1119.kanban.service;

import java.util.Objects;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.co1119.kanban.dto.request.CardMoveRequest;
import com.co1119.kanban.entity.Card;
import com.co1119.kanban.entity.TaskList;
import com.co1119.kanban.entity.User;
import com.co1119.kanban.repository.CardRepository;
import com.co1119.kanban.repository.TaskListRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final TaskListRepository taskListRepository;
    private final UserService userService;

    @Override
    @Transactional
    public Card moveCard(Long cardId, CardMoveRequest request) {
        Card card = cardRepository.findById(cardId).orElseThrow(() -> new RuntimeException("カードが見つかりません"));

        TaskList newTaskList = taskListRepository.findById(request.getNewTaskListId())
                .orElseThrow(() -> new RuntimeException("タスクリストが見つかりません。"));

        User currentUser = userService.getCurrentUser();
        if (!Objects.equals(newTaskList.getBoard().getUser().getId(), currentUser.getId())) {
            throw new AccessDeniedException("タスクリストにアクセスする権限がありません。");
        }

        card.setTaskList(newTaskList);
        card.setOrderIndex(request.getNewOrderIndex());
        return cardRepository.save(card);
    }

}
