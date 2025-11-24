
/**
 * Boardオブジェクトの型定義です。
 */
export interface Board {
  id: number;
  title: string;
  taskLists: TaskList[];
}

/**
 * Cardオブジェクトの型定義です。
 */
export interface Card {
  id: number;
  title: string;
  orderIndex: number;
  description?: string | null;
  dueDate?: string | null; // yyyy-MM-dd
}

/**
 * TaskListオブジェクトの型定義です。
 */
export interface TaskList {
  id: number;
  title: string;
  orderIndex: number;
  cards: Card[];
}
