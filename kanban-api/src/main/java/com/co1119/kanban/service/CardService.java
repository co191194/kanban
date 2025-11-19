package com.co1119.kanban.service;

import com.co1119.kanban.dto.request.CardMoveRequest;
import com.co1119.kanban.entity.Card;

public interface CardService {
    /**
     * カードを移動します。
     * 
     * @param cardId
     * @param request
     * @return
     */
    Card moveCard(Long cardId, CardMoveRequest request);
}
