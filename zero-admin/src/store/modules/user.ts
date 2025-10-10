import { defineStore } from "pinia";
import { store, router, resetRouter, routerArrays } from "../utils";
import { useMultiTagsStoreHook } from "./multiTags";
import {
  setToken,
  getUser,
  getRoles,
  getPerms,
  setUser,
  setRoles,
  setPerms,
  getToken,
  clearAuth
} from "@/utils/auth";
import { type Login, login, logout, type OAuth2 } from "@/api/auth/oauth2";
import { type UserData, userInfo } from "@/api/auth/user";
import { rolePerm } from "@/api/auth/rbac";
import type { RoleData } from "@/api/auth/role";
import type { PermData } from "@/api/auth/perm";

export const useUserStore = defineStore("zero-user", {
  state: () => ({
    user: getUser() || (null as unknown as UserData),
    roles: getRoles() || (null as unknown as string[]),
    perms: getPerms() || (null as unknown as string[]),
    userMap: new Map<string, UserData>(),
    // 判断登录页面显示哪个组件（0：登录（默认）、1：手机登录、2：二维码登录、3：注册、4：忘记密码）
    currentPage: 0
  }),
  actions: {
    setUser(userData: UserData) {
      this.user = userData;
      setUser(userData);
    },

    setRoles(roles: string[]) {
      this.roles = roles;
      setRoles(roles);
    },

    setPerms(perms: string[]) {
      this.perms = perms;
      setPerms(perms);
    },

    /** 存储登录页面显示哪个组件 */
    SET_CURRENTPAGE(value: number) {
      this.currentPage = value;
    },

    async getUser() {
      if (this.user) {
        return this.user;
      }
      const token = getToken();
      if (token) {
        await this.userInfo();
        return this.user;
      }
      return null as unknown as UserData;
    },

    async getRoles() {
      if (this.roles) {
        return this.roles;
      }
      const token = getToken();
      if (token) {
        await this.rolePerm();
        return this.roles;
      }
      return null as unknown as string[];
    },

    async getPerms() {
      if (this.perms) {
        return this.perms;
      }
      const token = getToken();
      if (token) {
        await this.rolePerm();
        return this.perms;
      }
      return null as unknown as string[];
    },

    /** 登入 */
    async login(param: Login) {
      await login({ param }).then(({ data }) => {
        const oAuth2: OAuth2 = data;
        setToken(oAuth2);
      });
      // await this.refresh();
    },

    async refresh() {
      await this.userInfo();
      await this.rolePerm();
    },

    async userInfo() {
      await userInfo({ param: {} }).then(({ data }) => {
        const user: UserData = data;
        this.setUser(user);
      });
    },

    async rolePerm() {
      await rolePerm({ param: {} }).then(({ data }) => {
        const roleDataList: RoleData[] = data.roleDataList;
        const roles = roleDataList.map(v => v.code);
        this.setRoles(roles);
        const permDataList: PermData[] = data.permDataList;
        const perms = permDataList.map(v => v.code);
        this.setPerms(perms);
      });
    },

    /** 前端登出（不调用接口） */
    async logout() {
      await logout({ param: {} });
      this.user = null as unknown as UserData;
      this.roles = null as unknown as string[];
      this.perms = null as unknown as string[];
      clearAuth();
      useMultiTagsStoreHook().handleTags("equal", [...routerArrays]);
      resetRouter();
      await router.push("/login");
    }

    /** 刷新`token` */
    // async handRefreshToken(data) {
    //   return new Promise<RefreshTokenResult>((resolve, reject) => {
    //     refreshTokenApi(data)
    //       .then(data => {
    //         if (data) {
    //           setToken(data.data);
    //           resolve(data);
    //         }
    //       })
    //       .catch(error => {
    //         reject(error);
    //       });
    //   });
    // }
  }
});

export function useUserStoreHook() {
  return useUserStore(store);
}
