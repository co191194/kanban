package com.co1119.kanban.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.co1119.kanban.dto.request.CardRequest;
import com.co1119.kanban.dto.request.ListMoveRequest;
import com.co1119.kanban.dto.response.AbstractResponse;
import com.co1119.kanban.dto.response.CardResponse;
import com.co1119.kanban.dto.response.ListResponse;
import com.co1119.kanban.entity.Card;
import com.co1119.kanban.entity.TaskList;
import com.co1119.kanban.service.TaskListService;
import com.co1119.kanban.utility.ResponseUtility;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/lists")
@RequiredArgsConstructor
public class TaskListController {

    private final TaskListService taskListService;

    /**
     * タスクリストに紐づく新しいカードを作成します。
     * 
     * @param listId
     * @param request
     * @return
     */
    @PostMapping("/{listId}/cards")
    ResponseEntity<? extends AbstractResponse> createCard(@PathVariable Long listId, @RequestBody CardRequest request) {
        try {
            Card newCard = taskListService.createCard(listId, request);
            return ResponseUtility.createCreatedReponse(CardResponse.createCardResponse(newCard));
        } catch (AccessDeniedException e) {
            return ResponseUtility.createForbbidenResponse(e);
        } catch (RuntimeException e) {
            return ResponseUtility.createNotFoundResponse(e);
        }
    }

    /**
     * タスクリストを移動します。
     * 
     * @param listId
     * @param request
     * @return
     */
    @PutMapping("/{listId}/move")
    public ResponseEntity<? extends AbstractResponse> moveLList(@PathVariable Long listId,
            @RequestBody ListMoveRequest request) {
        try {
            TaskList taskList = taskListService.moveList(listId, request);
            return ResponseUtility.createOkResponse(ListResponse.createTaskListResponse(taskList));
        } catch (Exception e) {
            return ResponseUtility.createBadRequestResponse(e);
        }
    }
}
