import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../auth.service';
import { EmergencyService, Emergency } from '../emergency.service';

@Component({
  selector: 'app-driver-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './driver-dashboard.component.html',
  styleUrls: ['./driver-dashboard.component.css']
})
export class DriverDashboardComponent implements OnInit {
  role: string = 'DRIVER';
  driverName: string = 'Driver Dan';
  driverStatus: 'available' | 'on-trip' | 'offline' = 'available';
  assignedReports: Emergency[] = [];
  currentLocation: string = 'City Center';

  constructor(
    private authService: AuthService,
    private router: Router,
    private emergencyService: EmergencyService
  ) {}

  ngOnInit(): void {
    this.fetchAssignedReports();
  }

  fetchAssignedReports() {
    const driverId = this.authService.getLoggedInUserId();
    if (driverId) {
      this.emergencyService.getAssignedEmergencies(driverId).subscribe({
        next: (emergencies) => {
          this.assignedReports = emergencies;
        },
        error: (error) => {
          console.error('Error loading assigned reports:', error);
        }
      });
    }
  }

  setDriverStatus(status: 'available' | 'on-trip' | 'offline') {
    this.driverStatus = status;
    console.log(`Driver status set to: ${status}`);
  }

  updateReportStatus(reportId: number, newStatus: 'ASSIGNED' | 'RESOLVED') {
    const report = this.assignedReports.find(r => r.id === reportId);
    if (report) {
      report.status = newStatus;
      console.log(`Report ${reportId} status updated to: ${newStatus}`);
      if (newStatus === 'RESOLVED') {
        const driverId = this.authService.getLoggedInUserId();
        if (driverId) {
          this.emergencyService.completeEmergency(reportId, driverId).subscribe(() => {
            this.fetchAssignedReports();
          });
        }
      }
    }
  }

  getPriorityClass(priority: string) {
    return `priority-${priority}`;
  }

  getStatusClass(status: string) {
    return `status-${status}`;
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
