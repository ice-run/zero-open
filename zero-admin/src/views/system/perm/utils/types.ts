import type { PermUpsert } from "@/api/auth/perm";

interface FormItemProps extends PermUpsert {
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
