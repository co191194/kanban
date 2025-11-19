package com.co1119.kanban.service;

import com.co1119.kanban.dto.request.CardRequest;
import com.co1119.kanban.dto.request.ListMoveRequest;
import com.co1119.kanban.entity.Card;
import com.co1119.kanban.entity.TaskList;

public interface TaskListService {
    /**
     * タスクリストに紐づく新規カードを作成します。
     * 
     * @param listId
     * @param request
     * @return
     */
    public Card createCard(Long listId, CardRequest request);

    /**
     * タスクリストの順序を移動します。
     * 
     * @param listId
     * @param request
     * @return
     */
    public TaskList moveList(Long listId, ListMoveRequest request);
}
