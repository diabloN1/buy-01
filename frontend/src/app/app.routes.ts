import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { roleGuard } from './core/guards/role.guard';

export const APP_ROUTES: Routes = [
  { path: '', pathMatch: 'full', loadComponent: () => import('./features/products/pages/home.page').then(m => m.HomePage) },
  { path: 'products', loadComponent: () => import('./features/products/pages/product-list.page').then(m => m.ProductListPage) },
  { path: 'products/:id', loadComponent: () => import('./features/products/pages/product-details.page').then(m => m.ProductDetailsPage) },
  { path: 'auth/login', loadComponent: () => import('./features/auth/login/login.page').then(m => m.LoginPage) },
  { path: 'auth/register', loadComponent: () => import('./features/auth/register/register.page').then(m => m.RegisterPage) },
  {
    path: 'seller/products',
    canActivate: [authGuard, roleGuard(['SELLER'])],
    loadComponent: () => import('./features/products/pages/seller-products.page').then(m => m.SellerProductsPage),
  },
  { path: '**', loadComponent: () => import('./features/not-found/not-found.page').then(m => m.NotFoundPage) },
];
