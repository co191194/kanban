import type { ReactNode } from "react"

export default function Button(props: {
  children?: ReactNode,
  type?: "submit" | "reset" | "button"
  onClick?: React.MouseEventHandler<HTMLButtonElement>
}) {

  return <button
    type={props.type}
    className="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline w-full"
    onClick={props.onClick}
  >
    {props.children}
  </button>
}