import Button from "@/components/Button";
import Input from "@/components/Input";
import { BOARD_API_ENDPOINT } from "@/consts/apiConstants";
import type { Board } from "@/types/entity";
import type { BoardRequest } from "@/types/request";
import type { BoardResponse } from "@/types/response";
import apiClient from "@/utility/apiClient";
import { AxiosError } from "axios";
import React, { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";


export default function DashboardPage() {
  const navigate = useNavigate();

  // ボード一覧
  const [boards, setBoards] = useState<Board[]>([]);
  // ローディング状態
  const [isLoading, setLoading] = useState(true);
  // エラー
  const [error, setError] = useState("");

  // ボード作成用
  const [newBoardTitle, setNewBoardTitle] = useState("");

  // ボード一覧の取得
  useEffect(() => {
    const fetchBoards = async () => {
      setLoading(true);
      setError("");

      try {
        const response = apiClient.get<BoardResponse>(BOARD_API_ENDPOINT)
        setBoards((await response).data.boards)
      } catch (err) {
        console.error("ボードの取得に失敗しました：", err);
        if (err instanceof AxiosError && (err.response?.status === 401 || err.response?.status === 403)) {
          setError("認証エラー。再度ログインしてください。");
          handleLogout();
        } else {
          setError("ボードの読み込みに失敗しました。");
        }
      } finally {
        setLoading(false);
      }
    }
    fetchBoards();
  }, [])

  // ログアウト
  const handleLogout = () => {
    localStorage.removeItem("token");
    navigate("/login");
  };

  // 新規ボード作成
  const handleCreateBoard = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    // 空のタイトルは無視
    if (!newBoardTitle.trim()) return;

    try {
      const response = await apiClient.post<BoardResponse>(BOARD_API_ENDPOINT, {
        title: newBoardTitle
      } as BoardRequest);

      setBoards([...boards, response.data.board]);
      setNewBoardTitle("");
    } catch (err) {
      console.error("ボードの作成に失敗しました：", err);
      setError("ボードの作成に失敗しました。");
    }
  }

  if (isLoading) {
    return <div className="p-8">ロード中...</div>
  }

  return (
    <div className="min-h-screen bg-gray-100 p-8">
      {/* ヘッダー */}
      <header className="flex justify-between items-center mb-8">
        <h1 className="text-3xl font-bold text-gray-800">マイボード</h1>
        <button
          onClick={handleLogout}
          className="mt-6 bg-red-500 hover:bg-red-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline"
        >
          ログアウト
        </button>
      </header>

      {/* エラー表示 */}
      {error && <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded relative mb-4">{error}</div>}

      {/* 新規ボード作成フォーム */}
      <section>
        <form onSubmit={handleCreateBoard} className="flex gap-4">
          <Input
            id="newBoardTitle"
            type="text"
            value={newBoardTitle}
            placeholder="新しいボード名..."
            onChange={(event) => setNewBoardTitle(event.target.value)}
          />
          <Button type="submit">
            作成
          </Button>
        </form>
      </section>

      {/* ボード一覧 */}
      <section>
        <div className="grid grid-cols-1 md:grid-cols-3 lg:grid-cols-4 gap-6">
          {boards.map((board) => (
            <Link
              to={`/board/${board.id}`}
              key={board.id}
              className="bg-white rounded-lg shadow-md p-6 h-32 hover:shadow-lg transition-shadow cursor-pointer"
            >
              <h2 className="text-xl font-semibold text-gray-900">{board.title}</h2>
            </Link>
          ))}
        </div>

        {/* ボードが０件の場合 */}
        {
          !isLoading && boards.length === 0 && (
            <div className="text-center text-gray-500 mt-12">
              <p>ボードがありません。</p>
              <p>最初のボードを作成してみましょう！</p>
            </div>
          )
        }
      </section>

    </div>
  );
}
