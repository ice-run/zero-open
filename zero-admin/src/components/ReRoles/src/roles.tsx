import { defineComponent, Fragment } from "vue";
import { hasRoles } from "@/utils/auth";

export default defineComponent({
  name: "Roles",
  props: {
    value: {
      type: undefined,
      default: []
    }
  },
  setup(props, { slots }) {
    return () => {
      if (!slots) return null;
      return hasRoles(props.value) ? (
        <Fragment>{slots.default?.()}</Fragment>
      ) : null;
    };
  }
});
