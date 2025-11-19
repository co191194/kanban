// 順序のギャップ
const ORDER_GAP = 1000.0;

export const calcNewOrderIndex = (
  prevOrder: number | null,
  nextOrder: number | null
): number => {
  // リストが空の場合はギャップをそのまま渡す
  if (prevOrder === null && nextOrder === null) return ORDER_GAP;

  // 先頭に移動する場合は２番目からギャップを引いた値
  if (prevOrder === null) return nextOrder! - ORDER_GAP;

  // 末尾に移動する場合は末尾から２番目にギャップを足した値
  if (nextOrder === null) return prevOrder + ORDER_GAP;

  // 間に移動する場合は前後の平均
  return (prevOrder + nextOrder) / 2.0;

}