import type { Board, Card, TaskList } from "@/types/entity";

/**
 * カンバンAPI共通のレスポンスの型定義です。
 */
interface KanbanResponse {
  processResult: string;
  message: string;
}

/**
 * カンバン情報取得のレスポンスの型定義です。
 */
export interface BoardResponse extends KanbanResponse {
  boards: Board[];
  board: Board;
  taskList: TaskList;
}

export interface CardResponse extends KanbanResponse {
  card: Card;
}