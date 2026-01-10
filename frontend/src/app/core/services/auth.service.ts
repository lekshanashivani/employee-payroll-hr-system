import { Injectable, signal } from '@angular/core';
import { ApiService } from './api.service';
import { Router } from '@angular/router';
import { tap } from 'rxjs/operators';
import { Observable } from 'rxjs';

interface LoginResponse {
    token: string;
    email: string;
    role: string;
    userId: number;
    employeeId?: number;
}

@Injectable({
    providedIn: 'root'
})
export class AuthService {
    private readonly TOKEN_KEY = 'auth_token';
    private readonly USER_KEY = 'auth_user';

    // Signals for reactive state
    currentUser = signal<LoginResponse | null>(this.getUserFromStorage());
    isAuthenticated = signal<boolean>(!!this.getToken());

    constructor(private api: ApiService, private router: Router) { }

    login(credentials: any): Observable<LoginResponse> {
        return this.api.post<LoginResponse>('/auth/login', credentials).pipe(
            tap(response => this.setSession(response))
        );
    }

    logout() {
        localStorage.removeItem(this.TOKEN_KEY);
        localStorage.removeItem(this.USER_KEY);
        this.currentUser.set(null);
        this.isAuthenticated.set(false);
        this.router.navigate(['/login']);
    }

    getToken(): string | null {
        return localStorage.getItem(this.TOKEN_KEY);
    }

    private setSession(authResult: LoginResponse) {
        localStorage.setItem(this.TOKEN_KEY, authResult.token);
        localStorage.setItem(this.USER_KEY, JSON.stringify(authResult));
        this.currentUser.set(authResult);
        this.isAuthenticated.set(true);
    }

    private getUserFromStorage(): LoginResponse | null {
        const user = localStorage.getItem(this.USER_KEY);
        return user ? JSON.parse(user) : null;
    }
}
