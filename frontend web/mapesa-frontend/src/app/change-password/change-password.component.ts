import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../auth.service';

@Component({
  selector: 'app-change-password',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './change-password.component.html',
  styleUrls: ['./change-password.component.css']
})
export class ChangePasswordComponent {
  email = '';
  oldPassword = '';
  newPassword = '';
  error = '';

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  changePassword(): void {
    this.authService.changePassword({
      email: this.email,
      oldPassword: this.oldPassword,
      newPassword: this.newPassword
    }).subscribe({
      next: () => {
        this.router.navigate(['/login']);
      },
      error: (error) => {
        this.error = 'Failed to change password. Please check your credentials.';
        console.error('Error changing password:', error);
      }
    });
  }
}
