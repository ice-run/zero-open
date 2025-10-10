import type { IdParam, PageData, PageParam, Request, Response } from "@/api";
import { http } from "@/utils/http";

/** 权限搜索 传入权限查询参数，查询分页权限数据 */
export async function permSearch(
  request: Request<PageParam<PermSearch>>
): Promise<Response<PageData<PermData>>> {
  return http.post<
    Request<PageParam<PermSearch>>,
    Response<PageData<PermData>>
  >(`/auth/api/perm-search`, { data: request });
}

/** 权限写入 传入权限新增或修改参数 ，写入权限数据 */
export async function permUpsert(
  request: Request<PermUpsert>
): Promise<Response<PermData>> {
  return http.post<Request<PermUpsert>, Response<PermData>>(
    `/auth/api/perm-upsert`,
    { data: request }
  );
}

/** 权限查询 传入权限 ID，查询一条权限数据 */
export async function permSelect(
  request: Request<IdParam>
): Promise<Response<PermData>> {
  return http.post<Request<IdParam>, Response<PermData>>(
    `/auth/api/perm-select`,
    { data: request }
  );
}

export type PermData = {
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

export type PermUpsert = {
  /** id */
  id?: string;
  /** 角色名称 */
  name: string;
  /** 角色代码 */
  code: string;
  /** valid 是否有效 */
  valid?: boolean;
};

export type PermSearch = {
  /** ID 权限 ID */
  id?: string;
  /** 权限名称 */
  name?: string;
  /** 权限代码 */
  code?: string;
  /** valid 是否有效 */
  valid?: boolean;
};
