import { Injectable, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap, catchError, of } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface LoginRequest {
  email: string;
  password: string;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
}

export interface LoginError {
  message: string;
  status?: number;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly TOKEN_KEY = 'accessToken';

  private tokenSignal = signal<string | null>(localStorage.getItem(this.TOKEN_KEY));
  isLoggedIn = computed(() => !!this.tokenSignal());

  constructor(
    private http: HttpClient,
    private router: Router,
  ) {}

  login(credentials: LoginRequest): Observable<AuthResponse | LoginError> {
    return this.http
      .post<AuthResponse>(`${environment.apiUrl}/api/auth/login`, credentials, {
        withCredentials: false,
      })
      .pipe(
        tap((res) => {
          if (res?.accessToken) {
            localStorage.setItem(this.TOKEN_KEY, res.accessToken);
            this.tokenSignal.set(res.accessToken);
          }
        }),
        catchError((err) => {
          const msg = err?.error?.message || err?.status === 0
            ? 'Cannot reach server. Is the backend running at ' + environment.apiUrl + '?'
            : err?.error?.message || 'Invalid email or password';
          return of({ message: msg, status: err?.status });
        }),
      );
  }

  logout(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    this.tokenSignal.set(null);
    this.router.navigate(['/login']);
  }

  getToken(): string | null {
    return this.tokenSignal();
  }
}
