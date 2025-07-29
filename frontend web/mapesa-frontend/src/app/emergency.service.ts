import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Emergency {
  id: number;
  description: string;
  latitude: number;
  longitude: number;
  locationDescription: string;
  status: 'PENDING' | 'ASSIGNED' | 'RESOLVED';
  reportedAt: string;
  reporterId: number;
  dispatcherId?: number;
  driverId?: number;
}

@Injectable({
  providedIn: 'root'
})
export class EmergencyService {
  private apiUrl = 'http://localhost:8081/emergencies';

  constructor(private http: HttpClient) { }

  createEmergency(emergency: Omit<Emergency, 'id' | 'status' | 'reportedAt'>): Observable<Emergency> {
    return this.http.post<Emergency>(this.apiUrl, emergency);
  }

  getEmergenciesByReporter(reporterId: number): Observable<Emergency[]> {
    return this.http.get<Emergency[]>(`${this.apiUrl}/reporter/${reporterId}`);
  }

  getEmergenciesByDriver(driverId: number): Observable<Emergency[]> {
    return this.http.get<Emergency[]>(`${this.apiUrl}/driver/${driverId}`);
  }

  getAllEmergencies(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/all`);
  }

  getEmergencyDriver(id: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${id}/driver`);
  }

  approveEmergency(emergencyId: number, dispatcherId: number): Observable<Emergency> {
    return this.http.post<Emergency>(`${this.apiUrl}/${emergencyId}/approve/${dispatcherId}`, {});
  }

  rejectEmergency(emergencyId: number, dispatcherId: number): Observable<Emergency> {
    return this.http.post<Emergency>(`${this.apiUrl}/${emergencyId}/reject/${dispatcherId}`, {});
  }

  assignEmergency(emergencyId: number, driverId: number, dispatcherId: number): Observable<Emergency> {
    return this.http.post<Emergency>(`${this.apiUrl}/${emergencyId}/assign/${driverId}/${dispatcherId}`, {});
  }

  completeEmergency(emergencyId: number, driverId: number): Observable<Emergency> {
    return this.http.post<Emergency>(`${this.apiUrl}/${emergencyId}/complete/${driverId}`, {});
  }

  getAssignedEmergencies(driverId: number): Observable<Emergency[]> {
    return this.http.get<Emergency[]>(`${this.apiUrl}/driver/${driverId}/assigned`);
  }

  getEmergencyDriverByReporter(emergencyId: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/reporter/${emergencyId}/driver`);
  }

  getEmergency(id: number): Observable<Emergency> {
    return this.http.get<Emergency>(`${this.apiUrl}/${id}`);
  }
}
