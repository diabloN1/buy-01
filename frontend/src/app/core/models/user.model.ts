export type UserRole = "USER" | "SELLER" | "ADMIN";

export interface UserAvatar {
  id: string;
  url: string;
}

export interface User {
  id: string;
  email: string;
  name: string;
  role: UserRole;
  avatar?: UserAvatar;
  createdAt?: string;
}

export interface UserWidget {
  id: string;
  name: string;
  avatar?: UserAvatar;
}

export interface AuthResponse {
  token: string;
  user: User;
}

export interface LoginRequest {
  email: string;
  password: string;
}
export interface RegisterRequest {
  name: string;
  email: string;
  password: string;
  role: UserRole;
}
export interface UpdateProfileRequest {
  name?: string;
  avatarUrl?: string;
}
