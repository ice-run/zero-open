import { system } from "@/router/enums";

const Layout = () => import("@/layout/index.vue");

const systemRouter = {
  path: "/system",
  name: "System",
  component: Layout,
  redirect: "/system/user",
  meta: {
    icon: "ep:setting",
    title: "系统管理",
    rank: system
  },
  children: [
    {
      path: "/system/user",
      name: "System-User",
      component: () => import("@/views/system/user/index.vue"),
      meta: {
        icon: "ri:admin-line",
        title: "用户管理",
        keepAlive: true,
        showParent: true
      }
    },
    {
      path: "/system/role/index",
      name: "SystemRole",
      component: () => import("@/views/system/role/index.vue"),
      meta: {
        icon: "ri:admin-fill",
        title: "menus.pureRole",
        roles: ["admin"]
      }
    },
    {
      path: "/system/perm/index",
      name: "SystemPerm",
      component: () => import("@/views/system/perm/index.vue"),
      meta: {
        icon: "ep:menu",
        title: "menus.pureSystemPerm",
        roles: ["admin"]
      }
    },
    {
      path: "/system/group/index",
      name: "SystemGroup",
      component: () => import("@/views/system/group/index.vue"),
      meta: {
        icon: "ri:git-branch-line",
        title: "menus.pureGroup",
        roles: ["admin"]
      }
    }
  ]
};

export default systemRouter as RouteConfigsTable;
