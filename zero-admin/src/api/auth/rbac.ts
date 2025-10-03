import { http } from "@/utils/http";
import type { IdParam, No, Ok, Request, Response } from "@/api";
import type { RoleData } from "@/api/auth/role";
import type { PermissionData } from "@/api/auth/permission";

/** 自己的角色权限代码数组 @DaoDao header 中传入 token 信息，获取用户自己的角色权限信息，常用于前端获取数据加载到状态管理中。返回角色代码数组和权限代码数组 POST /api/role-permission */
export async function rolePermission(
  request: Request<No>
): Promise<Response<RolePermissionData>> {
  return http.post<Request<No>, Response<RolePermissionData>>(
    `/auth/api/role-permission`,
    { data: request }
  );
}

/** 查询用户的角色列表 */
export async function userRoleList(
  request: Request<IdParam>
): Promise<Response<RoleData[]>> {
  return http.post<Request<IdParam>, Response<RoleData[]>>(
    `/auth/api/user-role-list`,
    { data: request }
  );
}

/** 变更用户的角色 */
export async function userRoleUpsert(
  request: Request<UserRoleUpsert>
): Promise<Response<Ok>> {
  return http.post<Request<UserRoleUpsert>, Response<Ok>>(
    `/auth/api/user-role-upsert`,
    { data: request }
  );
}

export type RolePermissionData = {
  /** roleDataList 角色列表 */
  roleDataList: RoleData[];
  /** permissionDataList 权限列表 */
  permissionDataList: PermissionData[];
};

export type UserRoleUpsert = {
  /** 用户 ID */
  userId: string;
  /** 角色 ID */
  roleId: string;
  /** 是否有效 */
  valid?: boolean;
};
