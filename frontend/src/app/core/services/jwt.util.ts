import { UserRole } from "@core/models/user.model";

export interface JwtPayload {
  exp?: number;
  sub?: string;
  role?: UserRole;
  [k: string]: unknown;
}

export function decodeJwt(token: string): JwtPayload | null {
  try {
    const [, payload] = token.split(".");
    const decoded = atob(payload.replace(/-/g, "+").replace(/_/g, "/"));
    return JSON.parse(decoded) as JwtPayload;
  } catch {
    return null;
  }
}

export function isExpired(token: string): boolean {
  const p = decodeJwt(token);
  if (!p?.exp) return false;
  return Date.now() >= p.exp * 1000;
}
