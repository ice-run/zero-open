import type { IdParam, PageData, PageParam, Request, Response } from "@/api";
import { http } from "@/utils/http";

/** 权限搜索 传入权限查询参数，查询分页权限数据 */
export async function permissionSearch(
  request: Request<PageParam<PermissionSearch>>
): Promise<Response<PageData<PermissionData>>> {
  return http.post<
    Request<PageParam<PermissionSearch>>,
    Response<PageData<PermissionData>>
  >(`/auth/api/permission-search`, { data: request });
}

/** 权限写入 传入权限新增或修改参数 ，写入权限数据 */
export async function permissionUpsert(
  request: Request<PermissionUpsert>
): Promise<Response<PermissionData>> {
  return http.post<Request<PermissionUpsert>, Response<PermissionData>>(
    `/auth/api/permission-upsert`,
    { data: request }
  );
}

/** 权限查询 传入权限 ID，查询一条权限数据 */
export async function permissionSelect(
  request: Request<IdParam>
): Promise<Response<PermissionData>> {
  return http.post<Request<IdParam>, Response<PermissionData>>(
    `/auth/api/permission-select`,
    { data: request }
  );
}

export type PermissionData = {
  /** id */
  id: string;
  /** 权限名称 */
  name: string;
  /** 权限代码 */
  code: string;
  /** createTime 创建时间 */
  createTime: string;
  /** updateTime 更新时间 */
  updateTime: string;
  /** valid 是否有效 */
  valid: boolean;
};

export type PermissionUpsert = {
  /** id */
  id?: string;
  /** 角色名称 */
  name: string;
  /** 角色代码 */
  code: string;
  /** valid 是否有效 */
  valid?: boolean;
};

export type PermissionSearch = {
  /** ID 权限 ID */
  id?: string;
  /** 权限名称 */
  name?: string;
  /** 权限代码 */
  code?: string;
  /** valid 是否有效 */
  valid?: boolean;
};
