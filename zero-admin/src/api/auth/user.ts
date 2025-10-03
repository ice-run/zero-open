import { http } from "@/utils/http";
import type { No, PageData, PageParam, Request, Response } from "@/api";

/** 用户信息 header 中传入 token 信息，获取用户信息 */
export async function userInfo(
  request: Request<No>
): Promise<Response<UserData>> {
  return http.post<Request<No>, Response<UserData>>(`/auth/api/user-info`, {
    data: request
  });
}

/** 用户更新自己的信息 */
export async function userUpdate(
  request: Request<UserUpdate>
): Promise<Response<UserData>> {
  return http.post<Request<UserUpdate>, Response<UserData>>(
    `/auth/api/user-update`,
    { data: request }
  );
}

/** 查询用户 传入 id 查询用户信息 */
export async function userSelect(
  request: Request<UserSelect>
): Promise<Response<UserData>> {
  return http.post<Request<UserSelect>, Response<UserData>>(
    `/auth/api/user-select`,
    { data: request }
  );
}

/** 写入用户 传入用户信息，新增或更新一个用户 */
export async function userUpsert(
  request: Request<UserUpsert>
): Promise<Response<UserData>> {
  return http.post<Request<UserUpsert>, Response<UserData>>(
    `/auth/api/user-upsert`,
    { data: request }
  );
}

/** 搜索用户 传入用户信息，搜索用户列表 */
export async function userSearch(
  request: Request<PageParam<UserSearch>>
): Promise<Response<PageData<UserData>>> {
  return http.post<
    Request<PageParam<UserSearch>>,
    Response<PageData<UserData>>
  >(`/auth/api/user-search`, { data: request });
}

export type UserData = {
  /** username */
  username: string;
  /** nickname */
  nickname?: string;
  /** avatar */
  avatar?: string;
};

export type UserSearch = {
  /** ID 用户 ID */
  id?: string;
  /** username 用户名 */
  username?: string;
  /** nickname 昵称 */
  nickname?: string;
  /** valid 是否有效 */
  valid?: boolean;
};

export type UserSelect = {
  /** ID 用户 ID */
  id: string;
};

export type UserUpsert = {
  /** ID 用户 ID */
  id?: string;
  /** 用户名 仅用于用户标识，不包含任何实际业务信息 */
  username?: string;
  /** valid 是否有效 */
  valid?: boolean;
};

export type UserUpdate = {
  /** 昵称 */
  nickname?: string;
  /** 头像 */
  avatar?: string;
};
