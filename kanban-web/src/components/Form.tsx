import React, { type ReactNode } from "react";

export default function Form(props: {
  children: ReactNode,
  error: string,
  submitLabel: string,
  onSubmit: React.FormEventHandler<HTMLFormElement>
}) {
  return (
    <form
      onSubmit={props.onSubmit}>
      <div className="flex flex-col gap-3 mb-6">
        {props.children}
      </div>

      {props.error && <p className="text-red-500 text-xs italic mb-4">{props.error}</p>}

      <div className="flex items-center justify-between">
        <button
          type="submit"
          className="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline w-full">
          {props.submitLabel}
        </button>
      </div>
    </form>
  )
}