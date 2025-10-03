import { http } from "@/utils/http";
import type { No, Ok, Request, Response } from "@/api";

/** 获取验证码，此接口在白名单中，不需要验证 token */
export async function captchaCode(
  request: Request<No>
): Promise<Response<CaptchaData>> {
  return http.post<Request<No>, Response<CaptchaData>>(
    `/base/api/captcha-code`,
    {
      data: request
    }
  );
}

/** 校验验证码，此接口应当只允许后端微服务之间互相校验认证，不允许前端和 APP 与服务端之间进行校验。前端获取验证码，将用户输入的验证码，与后续接口的请求数据，一起传送给后端。由后端校验验证码。 */
export async function captchaCheck(
  request: Request<CaptchaParam>
): Promise<Response<Ok>> {
  return http.post<Request<CaptchaParam>, Response<Ok>>(
    `/base/api/captcha-check`,
    {
      data: request
    }
  );
}

export type CaptchaParam = {
  /** id 验证码 ID */
  id: string;
  /** code 验证码 code */
  code: string;
};

export type CaptchaData = {
  /** id 验证码 ID */
  id: string;
  /** image 验证码图片 base64 编码 */
  image: string;
};
