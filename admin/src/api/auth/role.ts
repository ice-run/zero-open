import type { IdParam, PageData, PageParam, Request, Response } from "@/api";
import { http } from "@/utils/http";

/** 角色搜索 传入角色查询参数，查询分页角色数据 */
export async function roleSearch(
  request: Request<PageParam<RoleSearch>>
): Promise<Response<PageData<RoleData>>> {
  return http.post<
    Request<PageParam<RoleSearch>>,
    Response<PageData<RoleData>>
  >(`/auth/api/role-search`, { data: request });
}

/** 角色写入 传入角色新增或修改参数 ，写入角色数据 */
export async function roleUpsert(
  request: Request<RoleUpsert>
): Promise<Response<RoleData>> {
  return http.post<Request<RoleUpsert>, Response<RoleData>>(
    `/auth/api/role-upsert`,
    { data: request }
  );
}

/** 角色查询 传入角色 ID，查询一条角色数据 */
export async function roleSelect(
  request: Request<IdParam>
): Promise<Response<RoleData>> {
  return http.post<Request<IdParam>, Response<RoleData>>(
    `/auth/api/role-select`,
    { data: request }
  );
}

export type RoleData = {
  /** id */
  id: string;
  /** 角色名称 */
  name: string;
  /** 角色代码 */
  code: string;
  /** createTime 创建时间 */
  createTime: string;
  /** updateTime 更新时间 */
  updateTime: string;
  /** valid 是否有效 */
  valid: boolean;
};

export type RoleUpsert = {
  /** id */
  id?: string;
  /** 角色名称 */
  name: string;
  /** 角色代码 */
  code: string;
  /** valid 是否有效 */
  valid?: boolean;
};

export type RoleSearch = {
  /** ID 角色 ID */
  id?: string;
  /** 角色名称 */
  name?: string;
  /** 角色代码 */
  code?: string;
  /** valid 是否有效 */
  valid?: boolean;
};
