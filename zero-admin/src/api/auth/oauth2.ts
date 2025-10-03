import { http } from "@/utils/http";
import type { No, Ok, Request, Response } from "@/api";

/** 登录 传入 username & password 信息，获取 token */
export async function login(
  request: Request<Login>
): Promise<Response<OAuth2>> {
  return http.post<Request<Login>, Response<OAuth2>>(`/auth/api/login`, {
    data: request
  });
}

/** 退出 header 中传入 token 信息，退出登录 */
export async function logout(request: Request<No>): Promise<Response<Ok>> {
  return http.post<Request<No>, Response<Ok>>(`/auth/api/logout`, {
    data: request
  });
}

export type Login = {
  /** username */
  username: string;
  /** password */
  password: string;
  /** captchaId 验证码 ID */
  captchaId: string;
  /** captchaCode 验证码 */
  captchaCode: string;
};

export type OAuth2 = {
  /** access_token */
  access_token: string;
  /** refresh_token */
  refresh_token: string;
  /** id_token */
  id_token: string;
  /** scope */
  scope: string;
  /** token_type */
  token_type: string;
  /** expires_in */
  expires_in: number;
};
