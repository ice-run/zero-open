import type { PermissionUpsert } from "@/api/auth/permission";

interface FormItemProps extends PermissionUpsert {
  id?: string;
  parentId?: string;
  name: string;
  code: string;
  valid?: boolean;
}
interface FormProps {
  formInline: FormItemProps;
}

export type { FormItemProps, FormProps };
