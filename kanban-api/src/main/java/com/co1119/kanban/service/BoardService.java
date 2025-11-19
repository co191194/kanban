package com.co1119.kanban.service;

import java.util.List;

import com.co1119.kanban.dto.request.BoardRequest;
import com.co1119.kanban.entity.Board;
import com.co1119.kanban.entity.TaskList;

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

    /**
     * ボードを取得します。
     * 
     * @param bordId
     * @return
     */
    Board getBoardById(Long boardId);

    /**
     * 新規タスクリストを作成します。
     * 
     * @param boardId
     * @param request
     * @return
     */
    TaskList createList(Long boardId, BoardRequest request);
}
