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
