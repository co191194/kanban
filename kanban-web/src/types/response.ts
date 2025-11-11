import type { Board } from "@/types/entity";

interface KanbanResponse {
  processResult: string;
}

export interface BoardSearchResponse extends KanbanResponse {
  boards: Board[];
}

export interface BoardCreateResponse extends KanbanResponse {
  board: Board;
}