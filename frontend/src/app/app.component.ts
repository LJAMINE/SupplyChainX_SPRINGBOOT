import { Component } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from './core/services/auth.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink, RouterLinkActive],
  template: `
    <div class="app-shell">
      @if (auth.isLoggedIn()) {
        <nav class="navbar">
          <a routerLink="/raw-materials" routerLinkActive="active" class="nav-link">Raw Materials</a>
          <a routerLink="/suppliers" routerLinkActive="active" class="nav-link">Suppliers</a>
          <span class="spacer"></span>
          <button type="button" class="btn-logout" (click)="logout()">Logout</button>
        </nav>
      }
      <main class="content">
        <router-outlet></router-outlet>
      </main>
    </div>
  `,
  styles: [`
    .app-shell { min-height: 100vh; display: flex; flex-direction: column; }
    .navbar {
      display: flex; align-items: center; gap: 1rem;
      padding: 0.75rem 1.5rem; background: #1e293b; border-bottom: 1px solid #334155;
    }
    .nav-link {
      color: #94a3b8; padding: 0.5rem 0.75rem; border-radius: 6px;
      transition: background 0.2s, color 0.2s;
    }
    .nav-link:hover, .nav-link.active { color: #38bdf8; background: #334155; }
    .spacer { flex: 1; }
    .btn-logout {
      background: #334155; color: #94a3b8; border: none; padding: 0.5rem 1rem; border-radius: 6px;
      font-size: 0.9rem;
    }
    .btn-logout:hover { background: #475569; color: #e2e8f0; }
    .content { flex: 1; padding: 1.5rem; }
  `],
})
export class AppComponent {
  constructor(public auth: AuthService) {}

  logout(): void {
    this.auth.logout();
  }
}
