import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { UserService } from '../user.service';
import { AuthService } from '../auth.service';
import { Inject, PLATFORM_ID } from '@angular/core';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  email = '';
  password = '';
  rememberMe = false;
  showPassword = false;
  isLoading = false;
  errorMessage = '';

  constructor(
    private router: Router,
    private authService: AuthService,
    @Inject(PLATFORM_ID) private platformId: Object
  ) { }

  login() {
    this.isLoading = true;
    this.errorMessage = ''; // Clear previous errors
    
    const credentials = { email: this.email, password: this.password };
    
    this.authService.login(credentials).subscribe({
      next: (response: any) => {
        this.isLoading = false;
        console.log('Login response:', response);
        
        // Store user data if needed (localStorage, sessionStorage, or service)
        this.authService.storeUser(response);
        
        // Navigate based on role (API returns uppercase roles)
        if (response && response.roles) {
          switch (response.roles.toLowerCase()) {
            case 'admin':
              this.router.navigate(['/admin-dashboard']);
              break;
            case 'reporter':
              this.router.navigate(['/reporter-dashboard']);
              break;
            case 'driver':
              this.router.navigate(['/driver-dashboard']);
              break;
            default:
              this.router.navigate(['/dashboard']); // Default dashboard
              break;
          }
        } else {
          // Handle cases where roles is not in the response
          this.errorMessage = 'Login successful, but no role information was provided.';
          this.router.navigate(['/dashboard']);
        }
      },
      error: (error: any) => {
        this.isLoading = false;
        console.error('Login error:', error);
        console.error('Error status:', error.status);
        console.error('Error message:', error.message);
        
        // Handle JSON parsing errors (HTML response instead of JSON)
        if (error.error instanceof SyntaxError && error.error.message.includes('Unexpected token')) {
          this.errorMessage = 'Server configuration error. Please check the API endpoint.';
        } else if (error.status === 401) {
          this.errorMessage = 'Invalid email or password';
        } else if (error.status === 0) {
          this.errorMessage = 'Unable to connect to server. Please try again.';
        } else if (error.status === 200 && error.error instanceof SyntaxError) {
          this.errorMessage = 'Server returned invalid response format.';
        } else {
          this.errorMessage = 'Login failed. Please try again.';
        }
      }
    });
  }

  togglePassword() {
    if (isPlatformBrowser(this.platformId)) {
      this.showPassword = !this.showPassword;
    }
  }
}
