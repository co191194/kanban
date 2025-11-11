package com.co1119.kanban.service;

import java.util.List;

import com.co1119.kanban.dto.request.BoardRequest;
import com.co1119.kanban.entity.Board;

public interface BoardService {

    /**
     * ログインユーザに紐づくのボード一覧を取得します。
     * 
     * @return
     */
    List<Board> getBoardsForCurrentUser();

    /**
     * ボードを作成して登録します。
     * 
     * @param boardRequest
     * @return
     */
    Board createBoard(BoardRequest boardRequest);

}
