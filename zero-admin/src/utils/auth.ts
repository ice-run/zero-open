import Cookies from "js-cookie";
import { useUserStoreHook } from "@/store/modules/user";
import { isString, isIncludeAllChildren } from "@pureadmin/utils";

import type { UserData } from "@/api/auth/user";
import type { OAuth2 } from "@/api/auth/oauth2";
import zero from "@/utils/zero";

const TOKEN = "ZERO-TOKEN";
const USER = "ZERO-USER";
const ROLES = "ZERO-ROLES";
const PERMISSIONS = "ZERO-PERMISSIONS";

const COOKIE_TOKEN = zero.ENV === "test" ? "TEST-" + TOKEN : TOKEN;
const DOMAIN = window.location.href.includes(".ice.run/")
  ? "ice.run"
  : window.location.hostname;

export interface DataInfo<T> {
  /** token */
  accessToken: string;
  /** `accessToken`的过期时间（时间戳） */
  expires: T;
  /** 用于调用刷新accessToken的接口时所需的token */
  refreshToken: string;
  /** 头像 */
  avatar?: string;
  /** 用户名 */
  username?: string;
  /** 昵称 */
  nickname?: string;
  /** 当前登录用户的角色 */
  roles?: Array<string>;
  /** 当前登录用户的按钮级别权限 */
  permissions?: Array<string>;
}

export const userKey = "user-info";
export const TokenKey = "authorized-token";
/**
 * 通过`multiple-tabs`是否在`cookie`中，判断用户是否已经登录系统，
 * 从而支持多标签页打开已经登录的系统后无需再登录。
 * 浏览器完全关闭后`multiple-tabs`将自动从`cookie`中销毁，
 * 再次打开浏览器需要重新登录系统
 * */
export const multipleTabsKey = "multiple-tabs";

/** 格式化token（jwt格式） */
export const formatToken = (token: string): string => {
  return "Bearer " + token;
};

export const hasRole = (value: string | Array<string>): boolean => {
  if (!value) return false;
  const allRoles = "*";
  const { roles } = useUserStoreHook();
  if (!roles) return false;
  if (roles.length === 1 && roles[0] === allRoles) return true;
  const isAuths = isString(value)
    ? roles.includes(value)
    : isIncludeAllChildren(value, roles);
  return isAuths ? true : false;
};

/** 是否有按钮级别的权限（根据登录接口返回的`permissions`字段进行判断）*/
export const hasPerms = (value: string | Array<string>): boolean => {
  if (!value) return false;
  const allPerms = "*:*:*";
  const { permissions } = useUserStoreHook();
  if (!permissions) return false;
  if (permissions.length === 1 && permissions[0] === allPerms) return true;
  const isAuths = isString(value)
    ? permissions.includes(value)
    : isIncludeAllChildren(value, permissions);
  return isAuths ? true : false;
};

export function getToken(): string {
  const sessionToken = localStorage.getItem(TOKEN);
  if (sessionToken) {
    return sessionToken;
  }
  const cookieToken = Cookies.get(COOKIE_TOKEN);
  if (cookieToken) {
    localStorage.setItem(TOKEN, cookieToken);
    return cookieToken;
  }
  return null as unknown as string;
}

export function setToken(oAuth2: OAuth2) {
  // const token = oAuth2.access_token;
  const token = oAuth2.id_token;
  localStorage.setItem(TOKEN, token);
  Cookies.set(COOKIE_TOKEN, token, {
    domain: DOMAIN,
    expires: new Date(new Date().getTime() + oAuth2.expires_in * 1000)
  });
}

export function removeToken() {
  localStorage.removeItem(TOKEN);
  Cookies.remove(COOKIE_TOKEN, {
    domain: DOMAIN
  });
}

export const authorization = (token: string): string => {
  return "Bearer " + token;
};

export function getUser(): UserData {
  const value = localStorage.getItem(USER);
  if (value) {
    return JSON.parse(value);
  }
  return null as unknown as UserData;
}

export function setUser(user: UserData): void {
  const value = JSON.stringify(user);
  localStorage.setItem(USER, value);
}

export function removeUser(): void {
  localStorage.removeItem(USER);
}

export function getRoles(): string[] {
  const value = localStorage.getItem(ROLES);
  if (value) {
    return JSON.parse(value);
  }
  return null as unknown as string[];
}

export function setRoles(roles: string[]): void {
  localStorage.setItem(ROLES, JSON.stringify(roles));
}

export function removeRoles() {
  localStorage.removeItem(ROLES);
}

export function getPermissions(): string[] {
  const value = localStorage.getItem(PERMISSIONS);
  if (value) {
    return JSON.parse(value);
  }
  return null as unknown as string[];
}

export function setPermissions(permissions: string[]) {
  localStorage.setItem(PERMISSIONS, JSON.stringify(permissions));
}

export function removePermissions() {
  localStorage.removeItem(PERMISSIONS);
}
