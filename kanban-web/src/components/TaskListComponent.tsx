import { LIST_API_ENDPOINT } from "@/consts/apiConstants";
import type { Card, TaskList } from "@/types/entity";
import type { CardResponse } from "@/types/response";
import apiClient from "@/utility/apiClient";
import { Draggable, Droppable } from "@hello-pangea/dnd";
import { useState } from "react";
import CardComponent from "./CardComponent";
import CreateItemForm from "./CreateItemForm";

interface TaskListProps {
  list: TaskList;
  index: number;
  onCardAdded: (updatedList: TaskList) => void;
  onCardClicked: (card: Card) => void;
}

function TaskListComponent({
  list,
  index,
  onCardAdded,
  onCardClicked
}: TaskListProps) {
  const [isAddingCard, setAddingCard] = useState(false);
  const [error, setError] = useState("");

  const handleCreateCard = async (title: string) => {
    setError("");

    try {
      const response = await apiClient.post<CardResponse>(`${LIST_API_ENDPOINT}/${list.id}/cards`, {
        title
      });
      const newCard = response.data.card;

      // 親コンポーネントに通知
      const updatedList = {
        ...list,
        cards: [...list.cards, newCard],
      };
      onCardAdded(updatedList);
      setAddingCard(false);
    } catch (err) {
      console.error("カードの作成に失敗しました。：", err);
      setError("カードの作成に失敗しました。");
    }
  };

  function getListId(list: TaskList): string {
    return `list-${list.id}`;
  }

  return (
    <Draggable draggableId={getListId(list)} index={index}>
      {(provided) => (
        <div
          {...provided.draggableProps}
          {...provided.dragHandleProps}
          ref={provided.innerRef}
          className="bg-gray-800 rounded-md w-72 shrink-0 flex flex-col"
        >
          <h2 className="font-semibold p-3">{list.title}</h2>

          <Droppable droppableId={getListId(list)} type="CARD">
            {(provided) => (
              <div
                {...provided.droppableProps}
                ref={provided.innerRef}
                className="grow p-3"
                style={{ minHeight: '50px', maxHeight: 'calc(100vh - 200px)', overflowY: 'auto' }}
              >
                {list.cards.map((card, index) => (
                  <CardComponent
                    key={card.id}
                    card={card}
                    index={index}
                    onClick={onCardClicked}
                  />
                ))}
                {provided.placeholder}
              </div>
            )}
          </Droppable>

          {/* 新規カード追加 */}
          <div className="p-3">
            {error && <p className="text-red-400 text-xs mb-2">{error}</p>}

            {isAddingCard ? (
              <CreateItemForm
                itemType="card"
                onSubmit={handleCreateCard}
                onCancel={() => setAddingCard(false)}
              />
            ) : (
              <button
                onClick={() => setAddingCard(true)}
                className="w-full text-left text-gray-400 hover:text-white hover:bg-gray-700 p-2 rounded"
              >
                + カードを追加
              </button>
            )}
          </div>
        </div>
      )}
    </Draggable>
  )
}

export default TaskListComponent;