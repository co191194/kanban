export default function Input(props: {
  id: string,
  type: React.HTMLInputTypeAttribute | undefined,
  value?: string | number | readonly string[],
  required?: boolean
  placeholder?: string
  onChange?: React.ChangeEventHandler<HTMLInputElement>,
}) {
  return (
    <input
      type={props.type}
      id={props.id}
      className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
      placeholder={props.placeholder}
      value={props.value}
      required={props.required}
      onChange={props.onChange}
    />
  )
}