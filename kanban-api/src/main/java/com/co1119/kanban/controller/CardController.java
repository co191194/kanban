package com.co1119.kanban.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.co1119.kanban.dto.request.CardMoveRequest;
import com.co1119.kanban.dto.response.AbstractResponse;
import com.co1119.kanban.dto.response.CardResponse;
import com.co1119.kanban.entity.Card;
import com.co1119.kanban.service.CardService;
import com.co1119.kanban.utility.ResponseUtility;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class CardController {
    private final CardService cardService;

    /**
     * カードを移動します。
     * 
     * @param cardId
     * @param request
     * @return
     */
    @PutMapping("/{cardId}/move")
    public ResponseEntity<? extends AbstractResponse> moveCard(@PathVariable Long cardId,
            @RequestBody CardMoveRequest request) {
        try {
            Card newCard = cardService.moveCard(cardId, request);
            return ResponseUtility.createOkResponse(CardResponse.createCardResponse(newCard));
        } catch (Exception e) {
            return ResponseUtility.createBadRequestResponse(e);
        }
    }
}
