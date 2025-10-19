// 虽然字段很少 但是抽离出来 后续有扩展字段需求就很方便了

import type { RoleUpsert } from "@/api/auth/role";

interface FormItemProps extends RoleUpsert {
  /** ID */
  id?: string;
  /** 角色名称 */
  name: string;
  /** 角色编码 */
  code: string;
}
interface FormProps {
  formInline: FormItemProps;
}

export type { FormItemProps, FormProps };
