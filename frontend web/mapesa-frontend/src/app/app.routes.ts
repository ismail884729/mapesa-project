import { Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { AdminDashboardComponent } from './admin-dashboard/admin-dashboard.component';
import { ReporterDashboardComponent } from './reporter-dashboard/reporter-dashboard.component';
import { DriverDashboardComponent } from './driver-dashboard/driver-dashboard.component';
import { ChangePasswordComponent } from './change-password/change-password.component';

export const routes: Routes = [
    { path: 'login', component: LoginComponent },
    { path: 'admin-dashboard', component: AdminDashboardComponent },
    { path: 'reporter-dashboard', component: ReporterDashboardComponent },
    { path: 'driver-dashboard', component: DriverDashboardComponent },
    { path: 'change-password', component: ChangePasswordComponent },
    { path: '', redirectTo: '/login', pathMatch: 'full' }
];
