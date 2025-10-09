import { hasRoles } from "@/utils/auth";
import type { Directive, DirectiveBinding } from "vue";

export const roles: Directive = {
  mounted(el: HTMLElement, binding: DirectiveBinding<string | Array<string>>) {
    const { value } = binding;
    if (value) {
      !hasRoles(value) && el.parentNode?.removeChild(el);
    } else {
      throw new Error(
        "[Directive: roles]: need roles! Like v-roles=\"['btn.add','btn.edit']\""
      );
    }
  }
};
