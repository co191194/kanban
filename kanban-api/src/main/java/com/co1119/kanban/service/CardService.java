package com.co1119.kanban.service;

import com.co1119.kanban.dto.request.CardMoveRequest;
import com.co1119.kanban.dto.request.CardUpdateRequest;
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

    /**
     * カードを更新します。
     * 
     * @param cardId
     * @param request
     * @return
     */
    public Card updateCard(Long cardId, CardUpdateRequest request);

    /**
     * カードを削除します。
     * 
     * @param cardId
     * @return
     */
    public Card deleteCard(Long cardId);
}
