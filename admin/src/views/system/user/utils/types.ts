import type { UserUpsert } from "@/api/auth/user";

interface FormItemProps extends UserUpsert {
  id?: string;
  /** 用于判断是`新增`还是`修改` */
  title: string;
  higherGroupOptions: Record<string, unknown>[];
  nickname: string;
  username: string;
  phone: string;
  email: string;
  valid: boolean;
  groupId: string;
  group?: {
    id?: number;
    name?: string;
  };
}
interface FormProps {
  formInline: FormItemProps;
}

interface RoleFormItemProps {
  id?: string;
  username: string;
  nickname: string;
  /** 角色列表 */
  roleOptions: any[];
  /** 选中的角色列表 */
  ids: Record<number, unknown>[];
}
interface RoleFormProps {
  formInline: RoleFormItemProps;
}

export type { FormItemProps, FormProps, RoleFormItemProps, RoleFormProps };
