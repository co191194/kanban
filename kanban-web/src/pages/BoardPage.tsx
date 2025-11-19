import { BOARD_API_ENDPOINT, CARD_API_ENDPOINT, LIST_API_ENDPOINT } from "@/consts/apiConstants";
import type { Board, TaskList } from "@/types/entity";
import type { BoardResponse } from "@/types/response";
import apiClient from "@/utility/apiClient";
import { AxiosError } from "axios";
import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom"
import { DragDropContext, Droppable, type DropResult } from "@hello-pangea/dnd"
import TaskListComponent from "@/components/TaskListComponent";
import CreateItemForm from "@/components/CreateItemForm";
import { calcNewOrderIndex } from "@/utility/orderUtility";
import type { CardMoveRequest, ListMoveRequest } from "@/types/request";

export default function BoardPage() {
  // URLパラメータを取得
  const { boardId } = useParams<{ boardId: string }>();

  const [board, setBoard] = useState<Board | null>(null);
  const [isLoading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string>("");

  const [isAddingList, setAddingList] = useState(false);

  useEffect(() => {
    if (!boardId) return;

    const fetchBoardData = async () => {
      setLoading(true);
      setError("");

      try {
        const response = await apiClient.get<BoardResponse>(`${BOARD_API_ENDPOINT}/${boardId}`);
        setBoard(response.data.board);
      } catch (error) {
        console.error("ボード情報の取得に失敗しました。：", error);
        if (error instanceof AxiosError && (error.response?.status === 401 || error.response?.status === 403)) {
          setError('認証エラー。再度ログインしてください。');
        } else if (error instanceof AxiosError && error.response?.status === 404) {
          setError('ボードが見つかりません。');
        } else {
          setError('ボードの読み込みに失敗しました。');
        }
      } finally {
        setLoading(false);
      }
    }
    fetchBoardData();
  }, [boardId])

  const onDragEnd = (result: DropResult) => {

    const { destination, source, draggableId, type } = result;

    // 無効なドロップは弾く
    if (!destination) return;
    if (destination.droppableId === source.droppableId &&
      destination.index === source.index) return;

    // ドロップされたアイテムの種類に応じて処理を呼び出す

    switch (type) {
      case "LIST":
        handleListMove(source.index, destination.index, draggableId);
        break;
      case "CARD":
        handleCardMove(source, destination, draggableId);
        break;
      default:
        // 上記に当てはまらない場合は何もしない
        return;
    }
  }

  const handleListMove = (
    sourceIndex: number,
    destIndex: number,
    draggableId: string
  ) => {
    if (!board) return;
    console.log("drop list!");
    console.log("sourceIndex: ", sourceIndex);
    console.log("destIndex: ", destIndex);
    console.log("draggableId: ", draggableId);

    // コピーを作成
    const newLists = Array.from(board.taskLists);
    // 移動対象を抜き出す
    const [movedList] = newLists.splice(sourceIndex, 1);
    // 移動先に挿入
    newLists.splice(destIndex, 0, movedList);

    // 新しい並び順を算出
    const prevOrderIndex = newLists.at(destIndex - 1)?.orderIndex ?? null;
    const nextOrderIndex = newLists.at(destIndex + 1)?.orderIndex ?? null;
    const newOrderIndex = calcNewOrderIndex(prevOrderIndex, nextOrderIndex)

    // DB更新前に描画を更新
    const listId = movedList.id;
    setBoard({
      ...board, taskLists: newLists.map(list =>
        list.id === listId ? { ...list, orderIndex: newOrderIndex } : list
      ),
    })

    // DB更新リクエスト
    apiClient.put(`${LIST_API_ENDPOINT}/${listId}/move`, { newOrderIndex } as ListMoveRequest)
      .catch(err => {
        console.error("リストの移動に失敗しました：", err);
        // TODO: エラー時は状態をもとに戻す
      })

  }

  const handleCardMove = (
    source: DropResult["source"],
    destination: DropResult["destination"],
    draggableId: string
  ) => {
    if (!(board && destination)) return;
    console.log("drop card!");
    console.log("source: ", source);
    console.log("destination: ", destination);
    console.log("draggable: ", draggableId);

    const cardId = Number(draggableId.split("-")[1]);
    const sourceListId = Number(source.droppableId.split("-")[1]);
    const destListId = Number(destination.droppableId.split("-")[1]);

    const sourceList = board.taskLists.find(list => list.id === sourceListId);
    let movedCard = sourceList?.cards.find(card => card.id === cardId);
    if (!(sourceList && movedCard)) return;

    let newBoard = { ...board };

    if (sourceListId === destListId) {
      // カード配列をコピーして移動後のカード配列を作成
      const newCards = Array.from(sourceList.cards);
      newCards.splice(source.index, 1);
      newCards.splice(destination.index, 0, movedCard);

      // 移動後の順序を算出
      const prevCardOrderIndex = newCards.at(destination.index - 1)?.orderIndex ?? null;
      const nextCardOrderIndex = newCards.at(destination.index + 1)?.orderIndex ?? null;
      const newOrderIndex = calcNewOrderIndex(prevCardOrderIndex, nextCardOrderIndex);

      // 画面に反映
      movedCard.orderIndex = newOrderIndex;
      const newList: TaskList = {
        ...sourceList,
        cards: newCards,
      };
      newBoard.taskLists = newBoard.taskLists.map(list =>
        list.id === sourceListId ? newList : list
      );
      setBoard(newBoard);

      // 更新のリクエスト
      apiClient.put(`${CARD_API_ENDPOINT}/${cardId}/move`, {
        newTaskListId: destListId,
        newOrderIndex
      } as CardMoveRequest)
        .catch(err => console.error("カードの移動に失敗：", err));
    } else {
      const destList = board.taskLists.find(list => list.id === destListId);
      if (!destList) return;

      // 移動元のリストから削除
      const newSourceCards = sourceList.cards.filter(card => card.id !== cardId);
      const newSourceList: TaskList = { ...sourceList, cards: newSourceCards };

      // 移動先のリストに挿入
      const newDestCards = Array.from(destList.cards);
      newDestCards.splice(destination.index, 0, movedCard);

      // 新しい順序を算出
      const prevCardOrder = newDestCards.at(destination.index - 1)?.orderIndex ?? null;
      const nextCardOrder = newDestCards.at(destination.index + 1)?.orderIndex ?? null;
      const newOrderIndex = calcNewOrderIndex(prevCardOrder, nextCardOrder);

      // 画面に反映
      movedCard.orderIndex = newOrderIndex;
      const newDestList: TaskList = { ...destList, cards: newDestCards };

      newBoard.taskLists = newBoard.taskLists.map(list => {
        if (list.id === sourceListId) {
          return newSourceList;
        } else if (list.id === destListId) {
          return newDestList;
        } else {
          return list;
        }
      });
      setBoard(newBoard);

      apiClient.put(`${CARD_API_ENDPOINT}/${cardId}/move`, {
        newTaskListId: destListId,
        newOrderIndex
      } as CardMoveRequest)
        .catch(err => console.error("カードの移動に失敗しました：", err));
    }

  }

  const handleCardAdded = (updatedList: TaskList) => {
    if (!board) return;

    // Board state 内の古い list を、新しい (カードが追加された) list に置き換える
    const newLists = board.taskLists.map(list =>
      list.id === updatedList.id ? updatedList : list
    );

    setBoard({
      ...board,
      taskLists: newLists,
    });
  };

  const handleCreateList = async (title: string) => {
    if (!boardId) return;

    try {
      const response = await apiClient.post<BoardResponse>(`${BOARD_API_ENDPOINT}/${boardId}/lists`, {
        title
      });
      const newList = response.data.taskList;

      if (board) {
        setBoard({
          ...board,
          taskLists: [...board.taskLists, newList],
        })
      }
      setAddingList(false)
    } catch (err) {
      console.error("リストの作成に失敗：", err);
      setError("リストの作成に失敗しました。");
    }
  }

  // レンダリング
  if (isLoading) {
    return <div className="p-8 text-white">読み込み中...</div>;
  }

  if (error) {
    return <div className="p-8 text-red-400">{error}</div>;
  }

  if (!board) {
    return <div className="p-8 text-white">ボードデータがありません。</div>;
  }
  return (
    <DragDropContext onDragEnd={onDragEnd}>
      <div className="flex flex-col h-screen bg-blue-900 text-white p-4">
        {/* ヘッダー */}
        <header className="mb-4">
          <Link to="/dashboard" className="text-gray-300 hover:text-white">&larr; ボード一覧に戻る</Link>
          <h1 className="text-3xl font-bold mt-2">{board.title}</h1>
        </header>

        {/* カンバン本体 (横スクロール) */}
        {/* `Droppable` (リストをドロップできるエリア) */}
        {/* type="LIST" と direction="horizontal" でリスト(カラム)自体のD&D(横移動)を制御 */}
        <Droppable droppableId="all-lists" direction="horizontal" type="LIST">
          {(provided) => (
            <div
              {...provided.droppableProps}
              ref={provided.innerRef}
              className="flex grow overflow-x-auto gap-4"
            >
              {/* --- リスト (カラム) のループ --- */}
              {board.taskLists.map((list, index) => (
                <TaskListComponent
                  key={list.id}
                  list={list}
                  index={index}
                  onCardAdded={handleCardAdded}
                />
              ))}
              {provided.placeholder} {/* ドロップエリアの拡張用 */}

              <div className="bg-gray-800 rounded-md w-72 shrink-0">
                {isAddingList ? (
                  <CreateItemForm
                    itemType="list"
                    onSubmit={handleCreateList}
                    onCancel={() => setAddingList(false)}
                  />
                ) : (
                  <button
                    onClick={() => setAddingList(true)}
                    className="w-full text-left text-gray-400 hover:text-white hover:bg-gray-700 p-3 rounded"
                  >
                    + 別のリストを追加
                  </button>
                )}

              </div>
            </div>


          )}
        </Droppable>

        {/* (TODO: リスト追加ボタンをここに追加) */}
      </div>

    </DragDropContext>
  );
} 