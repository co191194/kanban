package com.co1119.kanban.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.co1119.kanban.dto.request.BoardRequest;
import com.co1119.kanban.dto.response.BoardCreateResponse;
import com.co1119.kanban.dto.response.BoardSearchResponse;
import com.co1119.kanban.entity.Board;
import com.co1119.kanban.service.BoardService;
import com.co1119.kanban.utility.ResponseUtility;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @GetMapping
    public ResponseEntity<BoardSearchResponse> getBoards() {
        List<Board> boards = boardService.getBoardsForCurrentUser();
        return ResponseUtility.createSuccessResponse(new BoardSearchResponse(boards));
    }

    @PostMapping
    public ResponseEntity<BoardCreateResponse> createBoard(@RequestBody BoardRequest request) {
        Board newBoard = boardService.createBoard(request);
        return ResponseUtility.createSuccessResponse(new BoardCreateResponse(newBoard));
    }

}
