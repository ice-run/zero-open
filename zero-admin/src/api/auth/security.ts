import { http } from "@/utils/http";
import type { Ok, Request, Response } from "@/api";

/** 变更密码 输入 新密码和旧密码，设置用户密码 */
export async function changePassword(
  request: Request<ChangePassword>
): Promise<Response<Ok>> {
  return http.post<Request<ChangePassword>, Response<Ok>>(
    `/auth/api/change-password`,
    { data: request }
  );
}

/** 重置密码 输入 用户 id 和 密码，重新设置密码 */
export async function resetPassword(
  request: Request<ResetPassword>
): Promise<Response<Ok>> {
  return http.post<Request<ResetPassword>, Response<Ok>>(
    `/auth/api/reset-password`,
    { data: request }
  );
}

export type ResetPassword = {
  /** id user id */
  id: string;
  /** 密码 如果不传此字段，将使用用户名作为默认密码 */
  password?: string;
};

export type ChangePassword = {
  /** oldPassword 旧密码 */
  oldPassword: string;
  /** newPassword 新密码 */
  newPassword: string;
};
