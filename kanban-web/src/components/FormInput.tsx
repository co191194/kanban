import type React from "react";
import Input from "./Input";



export default function FormInput(props: {
  id: string,
  label: string,
  type: React.HTMLInputTypeAttribute | undefined,
  value: string,
  onChange: React.ChangeEventHandler<HTMLInputElement>,
  required?: boolean
}) {
  return (
    <div>
      <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor={props.id}>
        {props.label}
      </label>
      <Input
        type={props.type}
        id={props.id}
        value={props.value}
        onChange={props.onChange}
        required={props.required}
      />
    </div>
  )
}