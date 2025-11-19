package com.co1119.kanban.service;

import java.util.List;
import java.util.Objects;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.co1119.kanban.dto.request.BoardRequest;
import com.co1119.kanban.entity.Board;
import com.co1119.kanban.entity.TaskList;
import com.co1119.kanban.entity.User;
import com.co1119.kanban.repository.BoardRepository;
import com.co1119.kanban.repository.TaskListRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;
    private final TaskListRepository taskListRepository;

    private final UserService userService;

    @Override
    @Transactional(readOnly = true)
    public List<Board> getBoardsForCurrentUser() {
        User currentUser = userService.getCurrentUser();
        return boardRepository.findByUserId(currentUser.getId());
    }

    @Override
    @Transactional
    public Board createBoard(BoardRequest boardRequest) {
        User currentUser = userService.getCurrentUser();
        Board newBoard = new Board(boardRequest.getTitle(), currentUser);
        return boardRepository.save(newBoard);
    }

    @Override
    @Transactional(readOnly = true)
    public Board getBoardById(Long boardId) {
        User currentUser = userService.getCurrentUser();

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new RuntimeException("ボードが見つかりません。"));

        // 取得したボードがログインユーザのものでない場合はエラー終了
        if (!Objects.equals(board.getUser().getId(), currentUser.getId())) {
            throw new AccessDeniedException("権限がありません。");
        }

        return board;

    }

    @Override
    @Transactional
    public TaskList createList(Long boardId, BoardRequest request) {
        Board board = getBoardById(boardId);

        // D&Dでの入れ替え時は入れ替え先の前後の値の合計の1/2を割り当てるため
        // 大きな値を初期値として設定
        Double newOrderIndex = (board.getTaskLists().size() + 1) * 1000.0;

        TaskList newList = new TaskList(request.getTitle(), newOrderIndex, board);

        return taskListRepository.save(newList);
    }

}
