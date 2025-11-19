import type { Card } from "@/types/entity";
import { Draggable } from "@hello-pangea/dnd";
import React from "react";

interface CardProps {
  card: Card;
  index: number;
}

function CardComponent({ card, index }: CardProps) {
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
          className="bg-gray-700 rounded-md p-3 mb-3 shadow-sm hover:bg-gray-600"
        >
          {card.title}
        </div>
      )}
    </Draggable>
  )
}

export default React.memo(CardComponent);