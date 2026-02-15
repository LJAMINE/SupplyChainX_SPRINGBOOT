import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <div class="login-container">
      <div class="login-card">
        <h1 class="title">SupplyChainX</h1>
        <p class="subtitle">Sign in to continue</p>

        <form [formGroup]="form" (ngSubmit)="onSubmit()" class="login-form">
          @if (errorMessage) {
            <div class="error-msg">{{ errorMessage }}</div>
          }
          <div class="field">
            <label for="email">Email</label>
            <input
              id="email"
              type="email"
              formControlName="email"
              placeholder="you@example.com"
              autocomplete="email"
            />
            @if (form.get('email')?.invalid && form.get('email')?.touched) {
              <span class="field-error">Valid email required</span>
            }
          </div>
          <div class="field">
            <label for="password">Password</label>
            <input
              id="password"
              type="password"
              formControlName="password"
              placeholder="••••••••"
              autocomplete="current-password"
            />
            @if (form.get('password')?.invalid && form.get('password')?.touched) {
              <span class="field-error">Password required</span>
            }
          </div>
          <button type="submit" class="btn-submit" [disabled]="form.invalid || loading">
            {{ loading ? 'Signing in...' : 'Sign in' }}
          </button>
        </form>
      </div>
    </div>
  `,
  styles: [`
    .login-container {
      min-height: calc(100vh - 2rem);
      display: flex;
      align-items: center;
      justify-content: center;
      padding: 1rem;
    }
    .login-card {
      width: 100%;
      max-width: 400px;
      background: #1e293b;
      border-radius: 12px;
      padding: 2rem;
      border: 1px solid #334155;
      box-shadow: 0 10px 40px rgba(0,0,0,0.3);
    }
    .title { margin: 0 0 0.25rem; font-size: 1.75rem; font-weight: 700; color: #f8fafc; }
    .subtitle { margin: 0 0 1.5rem; color: #94a3b8; font-size: 0.95rem; }
    .login-form { display: flex; flex-direction: column; gap: 1.25rem; }
    .error-msg {
      padding: 0.75rem; background: rgba(239,68,68,0.15); color: #fca5a5;
      border-radius: 6px; font-size: 0.9rem;
    }
    .field { display: flex; flex-direction: column; gap: 0.4rem; }
    .field label { font-size: 0.875rem; font-weight: 500; color: #cbd5e1; }
    .field input {
      padding: 0.65rem 0.9rem; border: 1px solid #475569; border-radius: 6px;
      background: #0f172a; color: #e2e8f0; font-size: 1rem;
    }
    .field input:focus { outline: none; border-color: #38bdf8; }
    .field input::placeholder { color: #64748b; }
    .field-error { font-size: 0.8rem; color: #f87171; }
    .btn-submit {
      margin-top: 0.5rem;
      padding: 0.75rem 1rem;
      background: #38bdf8;
      color: #0f172a;
      border: none;
      border-radius: 6px;
      font-size: 1rem;
      font-weight: 600;
      transition: background 0.2s;
    }
    .btn-submit:hover:not(:disabled) { background: #7dd3fc; }
    .btn-submit:disabled { opacity: 0.6; cursor: not-allowed; }
  `],
})
export class LoginComponent {
  form: FormGroup;
  loading = false;
  errorMessage = '';

  constructor(
    private fb: FormBuilder,
    private auth: AuthService,
    private router: Router,
  ) {
    this.form = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required],
    });
  }

  onSubmit(): void {
    if (this.form.invalid || this.loading) return;
    this.loading = true;
    this.errorMessage = '';

    const credentials = {
      email: (this.form.value.email ?? '').trim(),
      password: this.form.value.password ?? '',
    };

    this.auth.login(credentials).subscribe({
      next: (res) => {
        this.loading = false;
        if (res && 'accessToken' in res) {
          this.router.navigate(['/raw-materials']);
        } else if (res && 'message' in res) {
          this.errorMessage = res.message;
        } else {
          this.errorMessage = 'Invalid email or password';
        }
      },
    });
  }
}
