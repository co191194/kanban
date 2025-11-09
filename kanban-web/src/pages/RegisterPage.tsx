import { useState } from "react";
import { useNavigate } from "react-router-dom";
import apiClient from "../utility/apiClient";
import { AxiosError } from "axios";
import FormInput from "@/components/FormInput";
import Form from "@/components/Form";


export default function RegisterPage() {
  const [email, setEmail] = useState<string>("");
  const [password, setPassword] = useState<string>("");
  const [error, setError] = useState<string>("");
  const navigate = useNavigate();

  const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setError("");

    try {
      const response = await apiClient.post("/api/auth/register", { email, password });
      console.debug("登録に成功しました：", response.data);

      // 登録に成功した場合はログイン画面に遷移
      navigate("/login");
    } catch (error) {
      console.error("新規登録でエラー", error);
      if (error instanceof AxiosError && error.response) {
        setError(error.response.data as string);
      } else {
        setError("新規登録に失敗しました。再度登録してください。")
      }
    }
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-100">
      <div className="bg-white p-8 rounded shadow-md w-full max-w-sm">
        <h2 className="text-2xl font-bold mb-6 text-center">新規登録</h2>
        <Form
          error={error}
          submitLabel="登録する"
          onSubmit={handleSubmit}>
          <FormInput
            id="email"
            label="メールアドレス"
            value={email}
            type="email"
            onChange={(event) => setEmail(event.target.value)}
            required
          />
          <FormInput
            id="password"
            label="パスワード"
            value={password}
            type="password"
            onChange={(event) => setPassword(event.target.value)}
            required
          />
        </Form>
      </div>

    </div>
  )
}