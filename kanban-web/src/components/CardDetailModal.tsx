import { CARD_API_ENDPOINT } from "@/consts/apiConstants";
import type { Card } from "@/types/entity";
import type { CardUpdateRequest } from "@/types/request";
import type { CardResponse } from "@/types/response";
import apiClient from "@/utility/apiClient";
import { useEffect, useState } from "react";


interface CardDetailModalProps {
  card: Card;
  onClose: () => void;
  onUpdate: (updatedCard: Card) => void;
  onDelete: (cardId: number) => void;
}

function CardDetailModal({ card, onClose, onUpdate, onDelete }: CardDetailModalProps) {
  const [title, setTitle] = useState(card.title);
  const [description, setDescription] = useState(card.description);
  const [dueDate, setDueDate] = useState(card.dueDate);

  // モーダル表示時にスクロールを固定する
  useEffect(() => {
    document.body.style.overflow = 'hidden';
    return () => {
      document.body.style.overflow = 'auto';
    }
  }, []);

  const handleSave = async () => {
    try {
      const response = await apiClient.put<CardResponse>(`${CARD_API_ENDPOINT}/${card.id}`, {
        title,
        description,
        dueDate,
      } as CardUpdateRequest);
      onUpdate(response.data.card);
      onClose();
    } catch (error) {
      console.error("カードの更新に失敗しました", error);
      alert("カードの更新に失敗しました");
    }
  };

  const handleDelete = async () => {
    try {
      await apiClient.delete<CardResponse>(`${CARD_API_ENDPOINT}/${card.id}`);
      onDelete(card.id);
      onClose();
    } catch (error) {
      console.error("カードの削除に失敗しました", error);
      alert("カードの削除に失敗しました");
    }
  };

  return (
    // モーダルのオーバーレイ
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50">
      {/* モーダルの本体 */}
      <div className="relative text-gray-900 bg-white rounded-lg shadow-lg w-full max-w-md p-6"
        role="dialog"
        onClick={(e) => e.stopPropagation()}
      >
        {/* モーダルのヘッダー */}
        <div className="flex items-center justify-between mb-4">
          <h2 className="text-lg font-medium">カード編集</h2>
          <button
            type="button"
            className="text-gray-400 hover:text-gray-500"
            onClick={onClose}
          >
            <span className="sr-only">Close</span>
            <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>
        {/* モーダルのコンテンツ */}
        <form onSubmit={handleSave}>
          <div className="mb-4">
            <label className="block text-sm font-medium mb-2">タイトル</label>
            <input
              type="text"
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>
          <div className="mb-4">
            <label className="block text-sm font-medium mb-2">説明</label>
            <textarea
              value={description ?? undefined}
              onChange={(e) => setDescription(e.target.value)}
              className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>
          <div className="mb-4">
            <label className="block text-sm font-medium mb-2">期限日</label>
            <input
              type="date"
              value={dueDate ?? undefined}
              onChange={(e) => setDueDate(e.target.value)}
              className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>


          {/* モーダルのフッター */}
          <div className="flex justify-between items-center">
            <button
              type="button"
              onClick={handleDelete}
              className="px-4 py-2 text-white bg-red-500 hover:bg-red-600 rounded-md"
            >
              削除
            </button>
            <div className="flex justify-end">
              <button
                type="button"
                onClick={onClose}
                className="px-4 py-2 text-gray-600 hover:text-gray-800"
              >
                キャンセル
              </button>
              <button
                type="submit"
                className="px-4 py-2 ml-2 text-white bg-blue-500 hover:bg-blue-600 rounded-md"
              >
                保存
              </button>
            </div>
          </div>
        </form>
      </div>
    </div>
  )
}

export default CardDetailModal;