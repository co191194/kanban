package com.co1119.kanban.service;

import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.co1119.kanban.dto.request.BoardRequest;
import com.co1119.kanban.entity.Board;
import com.co1119.kanban.entity.User;
import com.co1119.kanban.repository.BoardRepository;
import com.co1119.kanban.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    private final UserRepository userRepository;
    private final BoardRepository boardRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Board> getBoardsForCurrentUser() {
        User currentUser = getCurrentUser();
        return boardRepository.findByUserId(currentUser.getId());
    }

    @Override
    @Transactional
    public Board createBoard(BoardRequest boardRequest) {
        User currentUser = getCurrentUser();
        Board newBoard = new Board(boardRequest.getTitle(), currentUser);
        return boardRepository.save(newBoard);
    }

    private User getCurrentUser() {
        // コンテキストから認証情報を取得
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String email;
        if (principal instanceof UserDetails userDetails) {
            email = userDetails.getUsername();
        } else {
            email = principal.toString();
        }

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("ユーザが見つかりませんでした。"));
    }

}
