/**
 * 新規ボード作成のリクエスト
 */
export interface BoardRequest {
  title: string;
}

/**
 * リストの移動のリクエスト
 */
export interface ListMoveRequest {
  newOrderIndex: number;
}

/**
 * カードの移動のリクエスト
 */
export interface CardMoveRequest {
  newTaskListId: number;
  newOrderIndex: number;
}

export interface CardUpdateRequest {
  title: string;
  description?: string;
  dueDate?: string;
}