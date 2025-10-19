import CryptoJS from "crypto-js";
import { KEYUTIL, KJUR, type RSAKey } from "jsrsasign";

/**
 * 生成 n 位数字字符串
 * @param n 位数
 */
export function random(n: number): string {
  let s = "";
  for (let i = 0; i < n; i++) {
    s += Math.floor(Math.random() * 10);
  }
  return s;
}

/**
 * SHA256
 * @param text 文本
 * @returns 摘要
 */
export function sha256(text: string): string {
  return CryptoJS.SHA256(text).toString();
}

/**
 * AES 加密
 * @param plains 明文
 * @param key 密钥
 * @param iv 向量
 * @returns 密文
 */
export function aesEncrypt(plains: string, key: string, iv: string): string {
  const k = CryptoJS.enc.Utf8.parse(key);
  const i = CryptoJS.enc.Utf8.parse(iv);
  return CryptoJS.AES.encrypt(plains, k, { iv: i }).toString();
}

/**
 * AES 解密
 * @param cipher 密文
 * @param key 密钥
 * @param iv 向量
 * @returns 明文
 */
export function aesDecrypt(cipher: string, key: string, iv: string): string {
  const k = CryptoJS.enc.Utf8.parse(key);
  const i = CryptoJS.enc.Utf8.parse(iv);
  return CryptoJS.AES.decrypt(cipher, k, { iv: i }).toString(CryptoJS.enc.Utf8);
}

export function rsaEncrypt(plains: string, key: string): string {
  const publicKey = KEYUTIL.getKey(key);
  const cipher = KJUR.crypto.Cipher.encrypt(plains, publicKey as RSAKey, "RSA");
  return hexToBase64(cipher);
}

export function rsaDecrypt(
  cipher: string,
  key: string,
  password?: string
): string {
  const privateKey = KEYUTIL.getKey(key, password);
  return KJUR.crypto.Cipher.decrypt(cipher, privateKey as RSAKey, "RSA");
}

function hexToBase64(str: string): string {
  const strs = str
    .replace(/[\r\n]/g, "")
    .replace(/([\da-fA-F]{2}) ?/g, "0x$1 ")
    .replace(/ +$/, "")
    .split(" ");
  return btoa(
    String.fromCharCode.apply(
      null,
      strs.map(s => Number(s))
    )
  );
}

const RADIX_CHARS: string[] = [
  "0",
  "1",
  "2",
  "3",
  "4",
  "5",
  "6",
  "7",
  "8",
  "9",
  "A",
  "B",
  "C",
  "D",
  "E",
  "F",
  "G",
  "H",
  "I",
  "J",
  "K",
  "L",
  "M",
  "N",
  "O",
  "P",
  "Q",
  "R",
  "S",
  "T",
  "U",
  "V",
  "W",
  "X",
  "Y",
  "Z",
  "a",
  "b",
  "c",
  "d",
  "e",
  "f",
  "g",
  "h",
  "i",
  "j",
  "k",
  "l",
  "m",
  "n",
  "o",
  "p",
  "q",
  "r",
  "s",
  "t",
  "u",
  "v",
  "w",
  "x",
  "y",
  "z"
];

/**
 * Base conversion
 *
 * @param input Source string
 * @param fromBase Source base
 * @param toBase Target base
 * @return Target string
 */
export function radixConvert(
  input: string,
  fromBase: number,
  toBase: number
): string | null {
  const isValid: boolean = radixCheck(input, fromBase, toBase);
  if (!isValid) {
    return null;
  }
  const fromChars: string[] = RADIX_CHARS.slice(0, fromBase);
  const inputChars: string[] = input.split("");
  let num: bigint = BigInt(0);
  for (
    let i: number = 0, j: number = inputChars.length - 1;
    i < inputChars.length && j >= 0;
    i++, j--
  ) {
    const x: number = fromChars.indexOf(inputChars[i]);
    const d: bigint = BigInt(Math.pow(fromBase, j));
    num = num + BigInt(d * BigInt(x));
  }
  if (num === BigInt(0)) {
    return "0";
  }
  let result: string = "";
  while (num > BigInt(0)) {
    const [quotient, remainder]: bigint[] = [
      num / BigInt(toBase),
      num % BigInt(toBase)
    ];
    result = RADIX_CHARS[Number(remainder)] + result;
    num = quotient;
  }
  return result;
}

/**
 * Validate base conversion parameters
 *
 * @param input Source N-base string
 * @param fromBase Source base
 * @param toBase Target base
 * @return Whether the parameters are valid
 */
function radixCheck(input: string, fromBase: number, toBase: number): boolean {
  if (!input || input.length === 0) {
    return false;
  }
  if (
    fromBase <= 0 ||
    fromBase > RADIX_CHARS.length ||
    toBase <= 0 ||
    toBase > RADIX_CHARS.length
  ) {
    return false;
  }
  const fromChars = RADIX_CHARS.slice(0, fromBase);
  for (const c of input) {
    if (fromChars.indexOf(c) < 0) {
      return false;
    }
  }
  return true;
}

const security = {
  random,
  sha256,
  aesEncrypt,
  aesDecrypt,
  rsaEncrypt,
  rsaDecrypt,
  radixConvert
};

export default security;
