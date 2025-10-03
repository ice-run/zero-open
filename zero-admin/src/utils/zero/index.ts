const ENV = (() => {
  const href = window.location.href;
  // 检查是否是测试环境URL或本地开发环境
  if (
    href.startsWith("https://test-") ||
    href.includes("localhost") ||
    href.includes("127.0.0.1") ||
    /https?:\/\/(10\.\d{1,3}\.\d{1,3}\.\d{1,3})/.test(href) ||
    /https?:\/\/(192\.168\.\d{1,3}\.\d{1,3})/.test(href) ||
    /https?:\/\/(172\.(?:1[6-9]|2\d|3[01])\.\d{1,3}\.\d{1,3})/.test(href)
  ) {
    return "test";
  }
  return "prod";
})();

const zero = {
  ENV
};

export default zero;
