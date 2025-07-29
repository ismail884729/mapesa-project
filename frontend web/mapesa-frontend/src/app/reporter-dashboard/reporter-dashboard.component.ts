import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { EmergencyService, Emergency } from '../emergency.service';
import { AuthService } from '../auth.service';

@Component({
  selector: 'app-reporter-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './reporter-dashboard.component.html',
  styleUrls: ['./reporter-dashboard.component.css']
})
export class ReporterDashboardComponent implements OnInit {
  role: string = 'REPORTER';
  reporterName: string = 'John Reporter'; // Mock reporter name
  myReports: Emergency[] = [];
  
  showReportModal: boolean = false;
  newReport: Partial<Emergency> = {};

  constructor(
    private emergencyService: EmergencyService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadMyReports();
  }

  loadMyReports() {
    // Assuming reporterId is 1 for now
    this.emergencyService.getAllEmergencies().subscribe({
      next: (emergencies) => {
        this.myReports = emergencies.filter(e => e.reporter.id === 1);
      },
      error: (error) => {
        console.error('Error loading reports:', error);
      }
    });
  }

  openReportModal() {
    this.newReport = {
      description: '',
      locationDescription: '',
      latitude: 0,
      longitude: 0,
      reporterId: 1 // Mock reporter ID
    };
    this.showReportModal = true;
  }

  closeReportModal() {
    this.showReportModal = false;
  }

  submitReport() {
    if (!this.newReport.description || !this.newReport.locationDescription) {
      alert('Please fill all fields.');
      return;
    }

    this.emergencyService.createEmergency(this.newReport as any).subscribe({
      next: (emergency) => {
        // this.myReports.unshift(emergency);
        console.log('New report submitted:', emergency);
        this.closeReportModal();
      },
      error: (error) => {
        console.error('Error submitting report:', error);
        alert('Failed to submit report. Please try again.');
      }
    });
  }

  cancelReport(reportId: number) {
    console.log(`Cancelling report ${reportId}`);
    this.myReports = this.myReports.filter(report => report.id !== reportId);
  }

  getStatusClass(status: string) {
    switch (status) {
      case 'pending': return 'status-pending';
      case 'approved': return 'status-approved';
      case 'rejected': return 'status-rejected';
      case 'resolved': return 'status-resolved';
      default: return '';
    }
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  sendPanicReport(): void {
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition((position) => {
        const emergency: Partial<Emergency> = {
          description: 'PANIC BUTTON PRESSED',
          locationDescription: 'User activated panic button.',
          latitude: position.coords.latitude,
          longitude: position.coords.longitude,
          reporterId: 1 // Mock reporter ID
        };

        this.emergencyService.createEmergency(emergency as any).subscribe({
          next: (emergency) => {
            console.log('Panic report submitted:', emergency);
            alert('Panic report sent successfully!');
          },
          error: (error) => {
            console.error('Error submitting panic report:', error);
            alert('Failed to send panic report. Please try again.');
          }
        });
      });
    } else {
      alert('Geolocation is not supported by this browser.');
    }
  }
}
