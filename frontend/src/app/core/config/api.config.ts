import { environment } from "@env/environment";

export const API = {
  base: environment.apiBaseUrl,

  auth: {
    login: "/users/auth/login",
    register: "/users/auth/register",
  },

  users: {
    root: "/users",
    byId: (id: string) => `/users/${id}`,
  },

  products: {
    root: "/products",
    byId: (id: string) => `/products/${id}`,
  },

  media: {
    images: "/media/images",
    byId: (id: string) => `/media/images/${id}`,
  },
  profile: {
    me: "/me",
  },
} as const;
