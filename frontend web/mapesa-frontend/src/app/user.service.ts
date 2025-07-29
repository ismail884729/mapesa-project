import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';


export interface User {
  id: number;
  firstName: string;
  middleName: string;
  lastName: string;
  password?: string;
  phoneNumber: string;
  roles?: 'DRIVER' | 'REPORTER';
  email: string;
  address: string;
  licenseNumber?: string;
  status?: string;
  carId?: number;
  regionId?: number;
}

@Injectable({
  providedIn: 'root'
})
export class UserService {
   private apiUrl = 'http://localhost:8081/user';

  constructor(private http: HttpClient) { }

  getUsers(): Observable<User[]> {
    return this.http.get<User[]>(this.apiUrl);
  }

  getUser(id: number): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/${id}`);
  }

  createUser(user: Omit<User, 'id'>): Observable<User> {
    return this.http.post<User>(this.apiUrl, user);
  }

  createReporter(user: Omit<User, 'id'>): Observable<User> {
    return this.http.post<User>(`${this.apiUrl}/reporter`, user);
  }

  createDriver(user: Omit<User, 'id'>): Observable<User> {
    return this.http.post<User>(`${this.apiUrl}/driver`, user);
  }

  updateUser(id: number, user: Partial<User>): Observable<User> {
    return this.http.put<User>(`${this.apiUrl}/${id}`, user);
  }

  deleteUser(id: number): Observable<{}> {
    return this.http.delete(`${this.apiUrl}/${id}`);
  }

  getDrivers(): Observable<User[]> {
    return this.http.get<User[]>(`${this.apiUrl}/drivers`);
  }

  getDriver(id: number): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/drivers/${id}`);
  }

  getDriversByRegion(regionId: number): Observable<User[]> {
    return this.http.get<User[]>(`${this.apiUrl}/drivers/region/${regionId}`);
  }

  getActiveDrivers(): Observable<User[]> {
    return this.http.get<User[]>(`${this.apiUrl}/drivers/active`);
  }

}
