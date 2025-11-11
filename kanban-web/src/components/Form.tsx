import React, { type ReactNode } from "react";
import Button from "./Button";

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
        <Button>
          {props.submitLabel}
        </Button>
      </div>
    </form>
  )
}