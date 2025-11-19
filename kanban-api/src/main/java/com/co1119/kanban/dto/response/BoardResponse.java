package com.co1119.kanban.dto.response;

import java.util.List;

import com.co1119.kanban.entity.Board;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class BoardResponse extends AbstractResponse {
    private final List<Board> boards;
    private final Board board;

    public static BoardResponse createBoardsResponse(List<Board> boards) {
        return new BoardResponse(boards, null);
    }

    public static BoardResponse createBoardResponse(Board board) {
        return new BoardResponse(null, board);
    }

}
