package com.co1119.kanban.service;

import java.util.Objects;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.co1119.kanban.dto.request.CardRequest;
import com.co1119.kanban.dto.request.ListMoveRequest;
import com.co1119.kanban.entity.Card;
import com.co1119.kanban.entity.TaskList;
import com.co1119.kanban.entity.User;
import com.co1119.kanban.repository.CardRepository;
import com.co1119.kanban.repository.TaskListRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TaskListServiceImpl implements TaskListService {

    private final TaskListRepository taskListRepository;
    private final CardRepository cardRepository;

    private final UserService userService;

    @Override
    @Transactional
    public Card createCard(Long listId, CardRequest request) {
        TaskList taskList = taskListRepository.findById(listId)
                .orElseThrow(() -> new RuntimeException("タスクリストが見つかりません。"));

        // タスクリストが現在のユーザの所有でない場合はエラー終了
        User currentUser = userService.getCurrentUser();
        if (!Objects.equals(taskList.getBoard().getUser().getId(), currentUser.getId())) {
            throw new AccessDeniedException("タスクリストにアクセスする権限がありません。");
        }

        // 新規カードを登録
        // D&Dでの入れ替え時は入れ替え先の前後の値の合計の1/2を割り当てるため
        // 大きな値を初期値として設定
        Double newOrderIndex = (taskList.getCards().size() + 1) * 1000.0;
        Card newCard = new Card(request.getTitle(), newOrderIndex, taskList);
        return cardRepository.save(newCard);
    }

    @Override
    @Transactional
    public TaskList moveList(Long listId, ListMoveRequest request) {
        TaskList taskList = taskListRepository.findById(listId)
                .orElseThrow(() -> new RuntimeException("タスクリストが見つかりません。"));

        // タスクリストが現在のユーザの所有でない場合はエラー終了
        User currentUser = userService.getCurrentUser();
        if (!Objects.equals(taskList.getBoard().getUser().getId(), currentUser.getId())) {
            throw new AccessDeniedException("タスクリストにアクセスする権限がありません。");
        }

        // 順序を更新
        taskList.setOrderIndex(request.getNewOrderIndex());
        return taskListRepository.save(taskList);
    }

}
