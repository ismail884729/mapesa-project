import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { EmergencyService, Emergency } from '../emergency.service';
import { AuthService } from '../auth.service';
import Swal from 'sweetalert2';

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
  myReports: any[] = [];
  
  showReportModal: boolean = false;
  newReport: Partial<Emergency> = {};
  assignedDriver: any = null;
  showDriverModal: boolean = false;

  constructor(
    private emergencyService: EmergencyService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadMyReports();
  }

  loadMyReports() {
    const reporterId = this.authService.getLoggedInUserId();
    if (reporterId) {
      this.emergencyService.getEmergenciesByReporter(reporterId).subscribe({
        next: (emergencies) => {
          this.myReports = emergencies;
        },
        error: (error) => {
          console.error('Error loading reports:', error);
        }
      });
    }
  }

  openReportModal() {
    const reporterId = this.authService.getLoggedInUserId();
    if (reporterId) {
      this.newReport = {
        description: '',
        locationDescription: '',
        latitude: 0,
        longitude: 0,
        reporterId: reporterId
      };
      this.showReportModal = true;
    } else {
      alert('You must be logged in to report an emergency.');
    }
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
        this.myReports.unshift(emergency);
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
      case 'PENDING': return 'status-pending';
      case 'ASSIGNED': return 'status-approved';
      case 'REJECTED': return 'status-rejected';
      case 'RESOLVED': return 'status-resolved';
      default: return '';
    }
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  sendPanicReport(): void {
    const reporterId = this.authService.getLoggedInUserId();
    if (!reporterId) {
      Swal.fire('Error', 'You must be logged in to send a panic report.', 'error');
      return;
    }

    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition((position) => {
        Swal.fire({
          title: 'Submit New Report',
          html:
            '<textarea id="description" class="swal2-textarea" placeholder="Enter emergency description..."></textarea>',
          showCancelButton: true,
          confirmButtonText: 'Submit',
          preConfirm: () => {
            const description = (document.getElementById('description') as HTMLTextAreaElement).value;
            if (!description) {
              Swal.showValidationMessage('Description is required');
            }
            return { description: description };
          }
        }).then((result) => {
          if (result.isConfirmed) {
            const emergency: Partial<Emergency> = {
              description: result.value.description,
              locationDescription: 'User activated panic button.',
              latitude: position.coords.latitude,
              longitude: position.coords.longitude,
              reporterId: reporterId
            };

            this.emergencyService.createEmergency(emergency as any).subscribe({
              next: (emergency) => {
                console.log('Panic report submitted:', emergency);
                Swal.fire('Success', 'Panic report sent successfully!', 'success');
                this.loadMyReports();
              },
              error: (error) => {
                console.error('Error submitting panic report:', error);
                Swal.fire('Error', 'Failed to send panic report. Please try again.', 'error');
              }
            });
          }
        });
      });
    } else {
      Swal.fire('Error', 'Geolocation is not supported by this browser.', 'error');
    }
  }

  viewAssignedDriver(emergencyId: number) {
    this.emergencyService.getEmergencyDriverByReporter(emergencyId).subscribe({
      next: (driver) => {
        this.assignedDriver = driver;
        this.showDriverModal = true;
      },
      error: (error) => {
        console.error('Error loading assigned driver:', error);
        alert('Failed to load assigned driver. Please try again.');
      }
    });
  }

  closeDriverModal() {
    this.showDriverModal = false;
    this.assignedDriver = null;
  }
}
