package com.co1119.kanban.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.co1119.kanban.dto.request.BoardRequest;
import com.co1119.kanban.dto.response.AbstractResponse;
import com.co1119.kanban.dto.response.BoardResponse;
import com.co1119.kanban.dto.response.ListResponse;
import com.co1119.kanban.entity.Board;
import com.co1119.kanban.entity.TaskList;
import com.co1119.kanban.service.BoardService;
import com.co1119.kanban.utility.ResponseUtility;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    /**
     * 認証済みユーザのボード一覧を取得します。
     * 
     * @return
     */
    @GetMapping
    public ResponseEntity<BoardResponse> getBoards() {
        List<Board> boards = boardService.getBoardsForCurrentUser();
        return ResponseUtility.createOkResponse(BoardResponse.createBoardsResponse(boards));
    }

    /**
     * 認証済みユーザで新規ボードを作成します。
     * 
     * @param request
     * @return
     */
    @PostMapping
    public ResponseEntity<BoardResponse> createBoard(@RequestBody BoardRequest request) {
        Board newBoard = boardService.createBoard(request);
        return ResponseUtility.createCreatedReponse(BoardResponse.createBoardResponse(newBoard));
    }

    /**
     * 認証済みのユーザの特定のボードを取得します。
     * 
     * @param boardId
     * @return
     */
    @GetMapping("/{boardId}")
    public ResponseEntity<? extends AbstractResponse> getBoardById(@PathVariable Long boardId) {
        try {
            Board board = boardService.getBoardById(boardId);
            return ResponseUtility.createOkResponse(BoardResponse.createBoardResponse(board));
        } catch (AccessDeniedException e) {
            return ResponseUtility.createForbbidenResponse(e);
        } catch (RuntimeException e) {
            return ResponseUtility.createNotFoundResponse(e);
        }

    }

    /**
     * ボードに紐づく新規リストを作成します。
     * 
     * @param boardId
     * @param request
     * @return
     */
    @PostMapping("/{boardId}/lists")
    public ResponseEntity<? extends AbstractResponse> createList(@PathVariable Long boardId,
            @RequestBody BoardRequest request) {

        try {
            TaskList newList = boardService.createList(boardId, request);
            return ResponseUtility.createCreatedReponse(ListResponse.createTaskListResponse(newList));
        } catch (AccessDeniedException e) {
            return ResponseUtility.createForbbidenResponse(e);
        } catch (RuntimeException e) {
            return ResponseUtility.createNotFoundResponse(e);
        }
    }
}
