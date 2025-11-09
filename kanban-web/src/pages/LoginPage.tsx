import React, { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import apiClient from "@/utility/apiClient";
import { AxiosError } from "axios";
import Form from "@/components/Form";
import FormInput from "@/components/FormInput";

// バックエンドからのレスポンスの型を定義
interface AuthResponse {
  accessToken: string;
  // (tokenType など、他にあればここに追加)
}

export default function LoginPage() {
  const [email, setEmail] = useState<string>("");
  const [password, setPassword] = useState<string>("");
  const [error, setError] = useState<string>("");
  const navigate = useNavigate();

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    setError("");

    try {
      // apiClient.post のジェネリクスに応答の型を指定
      const response = await apiClient.post<AuthResponse>("/api/auth/login", { email, password });

      // 【重要】response.data が AuthResponse 型として扱われる
      const token = response.data.accessToken;
      localStorage.setItem("token", token);

      navigate("/dashboard");

    } catch (err) {
      console.error("Login error:", err);
      if (err instanceof AxiosError && err.response) {
        if (err.response.status === 401 || err.response.status === 403) {
          setError("メールアドレスまたはパスワードが正しくありません。");
        } else {
          setError(err.response.data as string || "Login failed.");
        }
      } else {
        setError("Login failed. Please try again.");
      }
    }
  };

  return (
    // (JSX/Tailwind の部分は RegisterPage と同様のため省略)
    <div className="min-h-screen flex items-center justify-center bg-gray-100">
      <div className="bg-white p-8 rounded shadow-md w-full max-w-sm">
        <h2 className="text-2xl font-bold mb-6 text-center">ログイン</h2>
        <Form
          error={error}
          submitLabel="ログイン"
          onSubmit={handleSubmit}>
          {/* Email Input */}
          <FormInput
            id="email"
            label="メールアドレス"
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
          />
          {/* Password Input */}
          <FormInput
            id="password"
            label="パスワード"
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />

        </Form>
        {/* Link to Register */}
        <p className="text-center text-gray-500 text-xs mt-6">
          アカウントをお持ちでないですか？
          <Link to="/register" className="text-blue-500 hover:text-blue-700">
            新規登録
          </Link>
        </p>
      </div>
    </div>
  );
}
