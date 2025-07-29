import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { CarService, Car } from '../car.service';
import { UserService, User } from '../user.service';
import { DashboardService, DashboardStats } from '../dashboard.service';
import { EmergencyService } from '../emergency.service';
import { AuthService } from '../auth.service';
import { catchError, of, forkJoin } from 'rxjs';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.css']
})
export class AdminDashboardComponent implements OnInit {
  activeTab: string = 'dashboard';
  notifications: number = 5;
  searchTerm: string = '';
  loading: boolean = false;
  error: string = '';

  dashboardStats: DashboardStats = {
    totalUsers: 0,
    activeDrivers: 0,
    availableCars: 0,
    pendingReports: 0
  };

  users: User[] = [];
  filteredUsers: User[] = [];
  reporters: User[] = [];
  drivers: User[] = [];
  cars: Car[] = [];
  emergencies: any[] = [];
  activeDrivers: any[] = [];
  availableDrivers: User[] = [];

  showModal: boolean = false;
  showConfirmModal: boolean = false;
  isEditing: boolean = false;
  modalTitle: string = '';
  modalType: string = '';
  currentItem: any = {};
  confirmMessage: string = '';
  itemToDelete: any = {};

  constructor(
    private carService: CarService,
    private userService: UserService,
    private dashboardService: DashboardService,
    private emergencyService: EmergencyService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadAllData();
  }

  loadAllData(): void {
    this.loading = true;
    this.error = '';

    // Load all data concurrently
    forkJoin({
      users: this.loadUsers(),
      drivers: this.loadDrivers(),
      cars: this.loadCars(),
      stats: this.loadDashboardStats(),
      emergencies: this.loadEmergencies()
    }).subscribe({
      next: (data) => {
        this.users = data.users;
        this.filteredUsers = [...this.users];
        this.drivers = data.drivers;
        this.cars = data.cars;
        this.dashboardStats = data.stats;
        this.emergencies = data.emergencies;
        
        // Filter reporters and available drivers
        this.reporters = this.users.filter(user => user.roles === 'REPORTER');
        this.availableDrivers = this.drivers.filter(driver => driver.status === 'available');
        
        // Initialize mock emergency reports with real data context
        this.initializeEmergencyReports();
        
        // Initialize active drivers for location tracking
        this.initializeActiveDrivers();
        
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading data:', error);
        this.error = 'Failed to load dashboard data. Please try again.';
        this.loading = false;
      }
    });
  }

  loadUsers() {
    return this.userService.getUsers().pipe(
      catchError(error => {
        console.error('Error loading users:', error);
        return of([]);
      })
    );
  }

  loadDrivers() {
    return this.userService.getDrivers().pipe(
      catchError(error => {
        console.error('Error loading drivers:', error);
        return of([]);
      })
    );
  }

  loadCars() {
    return this.carService.getCars().pipe(
      catchError(error => {
        console.error('Error loading cars:', error);
        return of([]);
      })
    );
  }

  loadDashboardStats() {
    return this.dashboardService.getDashboardStats().pipe(
      catchError(error => {
        console.error('Error loading dashboard stats:', error);
        return of({
          totalUsers: this.users.length,
          activeDrivers: this.drivers.filter(d => d.status === 'active').length,
          availableCars: this.cars.filter(c => c.status === 'available').length,
          pendingReports: 0
        });
      })
    );
  }

  loadEmergencies() {
    return this.emergencyService.getAllEmergencies().pipe(
      catchError(error => {
        console.error('Error loading emergencies:', error);
        return of([]);
      })
    );
  }

  initializeEmergencyReports(): void {
    // This method is now redundant as we are fetching real data.
    // We can keep it for context or remove it.
    // For now, let's just update the pending reports count.
    this.dashboardStats.pendingReports = this.emergencies.filter(e => e.emergency.status === 'PENDING').length;
  }

  initializeActiveDrivers(): void {
    // Convert real drivers to active driver format for location tracking
    this.activeDrivers = this.drivers.slice(0, 3).map((driver, index) => ({
      id: driver.id,
      name: `${driver.firstName} ${driver.lastName}`,
      avatar: `https://ui-avatars.com/api/?name=${driver.firstName}+${driver.lastName}&background=random`,
      status: driver.status || (index % 2 === 0 ? 'on-trip' : 'available'),
      location: driver.address || 'Location not specified',
      lastUpdate: `${Math.floor(Math.random() * 10) + 1} mins ago`,
      mapX: 25 + (index * 20),
      mapY: 40 + (index * 15),
      phone: driver.phoneNumber,
      email: driver.email
    }));
  }

  // Search functionality
  onSearchChange(): void {
    if (this.activeTab === 'users') {
      this.filteredUsers = this.users.filter(user => 
        user.firstName.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        user.lastName.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        user.email.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        user.phoneNumber.includes(this.searchTerm)
      );
    }
  }

  setActiveTab(tab: string): void {
    this.activeTab = tab;
    this.searchTerm = '';
    if (tab === 'users') {
      this.filteredUsers = [...this.users];
    }
  }

  getPageTitle(): string {
    switch (this.activeTab) {
      case 'dashboard': return 'Dashboard Overview';
      case 'users': return 'Users Management';
      case 'reporters': return 'Reporters Management';
      case 'drivers': return 'Drivers Management';
      case 'cars': return 'Cars Management';
      case 'reports': return 'Emergency Reports';
      case 'locations': return 'Live Driver Locations';
      default: return 'Admin Panel';
    }
  }

  toggleSidebar(): void {
    console.log('Sidebar toggled');
  }

  openModal(type: string, item: any = null): void {
    this.modalType = type;
    this.isEditing = !!item;
    this.modalTitle = `${this.isEditing ? 'Edit' : 'Add'} ${type.charAt(0).toUpperCase() + type.slice(1)}`;
    this.currentItem = this.isEditing ? { ...item } : this.getNewItemTemplate(type);
    this.showModal = true;
  }

  getNewItemTemplate(type: string): any {
    switch (type) {
      case 'user': 
        return { 
          firstName: '', 
          middleName: '', 
          lastName: '', 
          password: 'password',
          phoneNumber: '', 
          email: '', 
          address: ''
        };
      case 'driver': 
        return { 
          firstName: '', 
          middleName: '', 
          lastName: '', 
          password: 'password',
          phoneNumber: '', 
          email: '', 
          address: '', 
          licenseNumber: '', 
          status: 'ACTIVE', 
          carId: 0, 
          regionId: 1 
        };
      case 'car': 
        return { 
          plateNumber: '', 
          model: '', 
          color: '', 
          driver: '', 
          status: 'available' 
        };
      case 'reporter': 
        return { 
          firstName: '', 
          middleName: '', 
          lastName: '', 
          password: 'password',
          phoneNumber: '', 
          email: '', 
          address: ''
        };
      default: 
        return {};
    }
  }

  closeModal(): void {
    this.showModal = false;
    this.currentItem = {};
  }

  saveItem(): void {
    this.loading = true;

    if (this.currentItem.middleName === undefined) {
      this.currentItem.middleName = '';
    }
    
    if (this.modalType === 'car') {
      if (this.isEditing) {
        this.carService.updateCar(this.currentItem.id, this.currentItem).subscribe({
          next: () => {
            this.loadCars().subscribe(cars => {
              this.cars = cars;
              this.closeModal();
              this.loading = false;
            });
          },
          error: (error) => {
            console.error('Error updating car:', error);
            this.loading = false;
          }
        });
      } else {
        this.carService.createCar(this.currentItem).subscribe({
          next: () => {
            this.loadCars().subscribe(cars => {
              this.cars = cars;
              this.closeModal();
              this.loading = false;
            });
          },
          error: (error) => {
            console.error('Error creating car:', error);
            this.loading = false;
          }
        });
      }
    } else if (this.modalType === 'user' || this.modalType === 'driver' || this.modalType === 'reporter') {
      if (this.isEditing) {
        this.userService.updateUser(this.currentItem.id, this.currentItem).subscribe({
          next: () => {
            this.loadUsers().subscribe(users => {
              this.users = users;
              this.filteredUsers = [...users];
              if (this.modalType === 'driver') {
                this.loadDrivers().subscribe(drivers => this.drivers = drivers);
              }
              this.closeModal();
              this.loading = false;
            });
          },
          error: (error) => {
            console.error('Error updating user:', error);
            this.loading = false;
          }
        });
      } else {
        let createService;
        if (this.modalType === 'driver') {
          createService = this.userService.createDriver(this.currentItem);
        } else if (this.modalType === 'reporter' || this.modalType === 'user') {
          createService = this.userService.createReporter(this.currentItem);
        } else {
          createService = this.userService.createUser(this.currentItem);
        }

        createService.subscribe({
          next: () => {
            this.loadUsers().subscribe(users => {
              this.users = users;
              this.filteredUsers = [...users];
              if (this.modalType === 'driver') {
                this.loadDrivers().subscribe(drivers => this.drivers = drivers);
              }
              this.closeModal();
              this.loading = false;
            });
          },
          error: (error) => {
            console.error('Error creating user:', error);
            this.loading = false;
          }
        });
      }
    }
  }

  editItem(type: string, item: any): void {
    this.openModal(type, item);
  }

  deleteItem(type: string, id: number): void {
    this.confirmMessage = `Are you sure you want to delete this ${type}?`;
    this.itemToDelete = { type, id };
    this.showConfirmModal = true;
  }

  closeConfirmModal(): void {
    this.showConfirmModal = false;
    this.itemToDelete = {};
  }

  confirmAction(): void {
    this.loading = true;
    
    if (this.itemToDelete.type === 'car') {
      this.carService.deleteCar(this.itemToDelete.id).subscribe({
        next: () => {
          this.loadCars().subscribe(cars => {
            this.cars = cars;
            this.closeConfirmModal();
            this.loading = false;
          });
        },
        error: (error) => {
          console.error('Error deleting car:', error);
          this.loading = false;
        }
      });
    } else if (this.itemToDelete.type === 'user' || this.itemToDelete.type === 'driver' || this.itemToDelete.type === 'reporter') {
      this.userService.deleteUser(this.itemToDelete.id).subscribe({
        next: () => {
          this.loadUsers().subscribe(users => {
            this.users = users;
            this.filteredUsers = [...users];
            if (this.itemToDelete.type === 'driver') {
              this.loadDrivers().subscribe(drivers => this.drivers = drivers);
            }
            this.closeConfirmModal();
            this.loading = false;
          });
        },
        error: (error) => {
          console.error('Error deleting user:', error);
          this.loading = false;
        }
      });
    }
  }

  approveReport(emergencyId: number): void {
    // Assuming dispatcherId 1 for now
    this.emergencyService.approveEmergency(emergencyId, 1).subscribe(() => {
      this.loadEmergencies().subscribe(emergencies => this.emergencies = emergencies);
    });
  }

  rejectReport(emergencyId: number): void {
    // Assuming dispatcherId 1 for now
    this.emergencyService.rejectEmergency(emergencyId, 1).subscribe(() => {
      this.loadEmergencies().subscribe(emergencies => this.emergencies = emergencies);
    });
  }

  assignReport(emergencyId: number, driverId: number): void {
    // Assuming dispatcherId 1 for now
    this.emergencyService.assignEmergency(emergencyId, driverId, 1).subscribe(() => {
      this.loadEmergencies().subscribe(emergencies => this.emergencies = emergencies);
    });
  }

  completeReport(emergencyId: number, driverId: number): void {
    this.emergencyService.completeEmergency(emergencyId, driverId).subscribe(() => {
      this.loadEmergencies().subscribe(emergencies => this.emergencies = emergencies);
    });
  }

  refreshLocations(): void {
    // Simulate refreshing driver locations
    this.activeDrivers.forEach(driver => {
      driver.lastUpdate = 'Just now';
      driver.mapX = Math.random() * 80 + 10;
      driver.mapY = Math.random() * 60 + 20;
    });
  }

  contactDriver(id: number): void {
    const driver = this.activeDrivers.find(d => d.id === id);
    if (driver) {
      // In a real app, this would open a communication interface
      alert(`Contacting ${driver.name} at ${driver.phone}`);
    }
  }

  // Utility methods for template
  getDisplayName(user: User): string {
    return `${user.firstName} ${user.middleName || ''} ${user.lastName}`.trim();
  }

  getStatusClass(status: string): string {
    return status?.toLowerCase() || 'unknown';
  }

  getRoleClass(role: string): string {
    return role?.toLowerCase().replace(' ', '-') || 'unknown';
  }

  getPriorityClass(priority: string): string {
    return priority?.toLowerCase() || 'medium';
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
