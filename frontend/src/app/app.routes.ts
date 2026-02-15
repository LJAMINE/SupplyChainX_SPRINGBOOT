import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  { path: 'login', loadComponent: () => import('./features/login/login.component').then(m => m.LoginComponent) },
  {
    path: 'raw-materials',
    loadComponent: () => import('./features/raw-materials/raw-materials.component').then(m => m.RawMaterialsComponent),
    canActivate: [authGuard],
  },
  {
    path: 'suppliers',
    loadComponent: () => import('./features/suppliers/suppliers.component').then(m => m.SuppliersComponent),
    canActivate: [authGuard],
  },
  { path: '', redirectTo: 'raw-materials', pathMatch: 'full' },
  { path: '**', redirectTo: 'raw-materials' },
];
