export function emptyToNull<T>(value: T): T {
  // 处理null和undefined
  if (value === null || value === undefined) {
    return null as unknown as T;
  }

  // 处理字符串
  if (typeof value === "string") {
    return (value === "" ? null : value) as unknown as T;
  }

  // 处理数组
  if (Array.isArray(value)) {
    return value.map(item => emptyToNull(item)) as unknown as T;
  }

  // 处理对象（排除null，因为前面已经处理）
  if (typeof value === "object") {
    const result: Record<string, never> = {};

    for (const key in value) {
      if (Object.prototype.hasOwnProperty.call(value, key)) {
        // 递归处理每个属性
        result[key] = emptyToNull(value[key]) as never;
      }
    }

    return result as T;
  }

  // 其他类型直接返回
  return value;
}

export const sleep = (ms: number): Promise<void> => {
  return new Promise(resolve => setTimeout(resolve, ms));
};

export function getCookie(name: string): string | null {
  const match = document.cookie.match(new RegExp("(^| )" + name + "=([^;]+)"));
  if (match && match[2]) {
    return decodeURIComponent(match[2]);
  }
  return null;
}

/**
 * list 转 tree
 * list 中包含 id , code , name 属性，
 * 其中 code 的格式为 ^[0-9a-z-:]+$
 * 以 : 冒号分割，每个冒号分隔的元素为层级关系，
 * 例如：a:b:c 表示 a 下有 b ，b 下有 c
 * @param list
 * @param parentCode
 */
export const listToTree = (
  list: { id: string; code: string; name: string }[],
  parentCode: string = ""
): any[] => {
  const tree: any[] = [];
  for (const item of list) {
    if (item.code.startsWith(parentCode ? parentCode + ":" : "")) {
      const children = listToTree(list, item.code);
      tree.push(children.length > 0 ? { ...item, children } : { ...item });
    }
  }
  return tree;
};
