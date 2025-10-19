import type { GroupUpsert } from "@/api/auth/group";

interface FormItemProps extends GroupUpsert {
  higherGroupOptions: Record<string, unknown>[];
}
interface FormProps {
  formInline: FormItemProps;
}

export type { FormItemProps, FormProps };
