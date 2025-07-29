import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReporterDashboardComponent } from './reporter-dashboard.component';

describe('ReporterDashboardComponent', () => {
  let component: ReporterDashboardComponent;
  let fixture: ComponentFixture<ReporterDashboardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReporterDashboardComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ReporterDashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
