import type React from "react";



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
      <input
        type={props.type}
        id={props.id}
        className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
        onChange={props.onChange}
        required={props.required}
      />
    </div>
  )
}