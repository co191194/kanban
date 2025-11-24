import type { Card } from "@/types/entity";
import { Draggable } from "@hello-pangea/dnd";
import React from "react";

interface CardProps {
  card: Card;
  index: number;
  onClick: (card: Card) => void;
}

function CardComponent({ card, index, onClick }: CardProps) {
  return (
    <Draggable
      draggableId={`card-${card.id}`}
      index={index}
    >
      {(provided) => (
        <div
          {...provided.draggableProps}
          {...provided.dragHandleProps}
          ref={provided.innerRef}
          onClick={() => onClick(card)}
          className="bg-gray-700 rounded-md p-3 mb-3 shadow-sm hover:bg-gray-600 cursor-pointer"
        >
          {card.title}

          {/* 期限があれば表示する */}
          {card.dueDate && (
            <div className="text-xs text-gray-400 mt-1">
              📅 {card.dueDate}
            </div>
          )}
        </div>
      )}
    </Draggable>
  )
}

export default React.memo(CardComponent);