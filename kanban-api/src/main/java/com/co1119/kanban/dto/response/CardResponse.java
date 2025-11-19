package com.co1119.kanban.dto.response;

import com.co1119.kanban.entity.Card;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CardResponse extends AbstractResponse {
    private final Card card;

    public static CardResponse createCardResponse(Card card) {
        return new CardResponse(card);
    }
}
