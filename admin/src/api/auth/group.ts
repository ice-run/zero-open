import type { IdParam, PageData, PageParam, Request, Response } from "@/api";
import { http } from "@/utils/http";

/** 组织搜索 传入组织查询参数，查询分页组织数据 */
export async function groupSearch(
  request: Request<PageParam<GroupSearch>>
): Promise<Response<PageData<GroupData>>> {
  return http.post<
    Request<PageParam<GroupSearch>>,
    Response<PageData<GroupData>>
  >(`/auth/api/group-search`, { data: request });
}

/** 组织写入 传入组织新增或修改参数 ，写入组织数据 */
export async function groupUpsert(
  request: Request<GroupUpsert>
): Promise<Response<GroupData>> {
  return http.post<Request<GroupUpsert>, Response<GroupData>>(
    `/auth/api/group-upsert`,
    { data: request }
  );
}

/** 组织查询 传入组织 ID，查询一条组织数据 */
export async function groupSelect(
  request: Request<IdParam>
): Promise<Response<GroupData>> {
  return http.post<Request<IdParam>, Response<GroupData>>(
    `/auth/api/group-select`,
    { data: request }
  );
}

export type GroupData = {
  /** id */
  id: string;
  /** 父级 ID */
  parentId?: string;
  /** 组织名称 */
  name: string;
  /** 管理员 ID */
  adminId?: string;
  /** 管理员名称 */
  adminName?: string;
  /** createTime 创建时间 */
  createTime: string;
  /** updateTime 更新时间 */
  updateTime: string;
  /** valid 是否有效 */
  valid: boolean;
};

export type GroupUpsert = {
  /** id */
  id?: string;
  /** 父级 ID */
  parentId?: string;
  /** 组织名称 */
  name: string;
  /** 管理员 ID */
  adminId?: string;
  /** valid 是否有效 */
  valid?: boolean;
};

export type GroupSearch = {
  /** ID 组织 ID */
  id?: string;
  /** 组织名称 */
  name?: string;
  /** valid 是否有效 */
  valid?: boolean;
};
