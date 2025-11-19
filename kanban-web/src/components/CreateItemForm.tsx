import React, { useState } from "react";
import Button from "./Button";


interface CardItemFormProps {
  itemType: "card" | "list";
  onSubmit: (title: string) => void;
  onCancel: () => void;
}

function CreateItemForm({
  itemType,
  onSubmit,
  onCancel
}: CardItemFormProps) {
  const [title, setTitle] = useState("")

  const handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (!title.trim()) return;

    onSubmit(title);
    setTitle("");
  }

  let placeholder;
  let buttonText;
  if (itemType === "list") {
    placeholder = "リストのタイトルを入力...";
    buttonText = "リストを追加";
  } else {
    placeholder = "カードのタイトルを入力...";
    buttonText = "カードを追加";
  }

  return (
    <form onSubmit={handleSubmit} className="p-3">
      <textarea
        placeholder={placeholder}
        value={title}
        onChange={(event) => setTitle(event.target.value)}
        className="w-full p-2 rounded border-gray-500 border bg-gray-600 text-white focus:outline-none focus:ring-2 focus:ring-blue-500"
        rows={2}
        autoFocus
      />
      <div className="flex items-center gap-2 mt-2">
        <Button
          type="submit"
        >{buttonText}</Button>
        <button
          type="button"
          onClick={onCancel}
          className="text-gray-400 hover:text-white"
        >X</button>
      </div>
    </form>
  )
}

export default CreateItemForm;