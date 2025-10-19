import { monitor } from "@/router/enums";

const Layout = () => import("@/layout/index.vue");

const monitorRouter = {
  path: "/monitor",
  component: Layout,
  name: "Monitor",
  redirect: "/monitor/online-user",
  meta: {
    icon: "ep:monitor",
    title: "menus.pureSysMonitor",
    rank: monitor
  },
  children: [
    {
      path: "/monitor/online-user",
      component: () => import("@/views/monitor/online/index.vue"),
      name: "OnlineUser",
      meta: {
        icon: "ri:user-voice-line",
        title: "menus.pureOnlineUser",
        roles: ["admin"]
      }
    },
    {
      path: "/monitor/login-logs",
      component: () => import("@/views/monitor/logs/login/index.vue"),
      name: "LoginLog",
      meta: {
        icon: "ri:window-line",
        title: "menus.pureLoginLog",
        roles: ["admin"]
      }
    },
    {
      path: "/monitor/operation-logs",
      component: () => import("@/views/monitor/logs/operation/index.vue"),
      name: "OperationLog",
      meta: {
        icon: "ri:history-fill",
        title: "menus.pureOperationLog",
        roles: ["admin"]
      }
    },
    {
      path: "/monitor/system-logs",
      component: () => import("@/views/monitor/logs/system/index.vue"),
      name: "SystemLog",
      meta: {
        icon: "ri:file-search-line",
        title: "menus.pureSystemLog",
        roles: ["admin"]
      }
    }
  ]
};

export default monitorRouter as RouteConfigsTable;
