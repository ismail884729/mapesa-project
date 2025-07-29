import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

import { User } from './user.service'; // Assuming User interface is needed

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8081/user'; // Direct API URL

  constructor(private http: HttpClient) { }

  login(user: Pick<User, 'email' | 'password'>): Observable<any> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'Accept': 'application/json'
    });

    return this.http.post<any>(`${this.apiUrl}/login`, user, { headers });
  }

  changePassword(changePasswordRequest: { email: string, oldPassword: string, newPassword: string }): Observable<{}> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'Accept': 'application/json'
    });

    return this.http.post<{}>(`${this.apiUrl}/change-password`, changePasswordRequest, { headers });
  }

  getMe(): Observable<User> {
    const headers = new HttpHeaders({
      'Accept': 'application/json'
    });

    return this.http.get<User>(`${this.apiUrl}/me`, { headers });
  }

  logout(): void {
    // Clear user data from storage
  }
}
