import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet } from '@angular/router';
import { SidebarComponent } from '../../shared/components/sidebar/sidebar.component';

@Component({
    selector: 'app-main-layout',
    standalone: true,
    imports: [CommonModule, RouterOutlet, SidebarComponent],
    template: `
    <div class="layout-container">
      <app-sidebar></app-sidebar>
      <main class="main-content">
        <router-outlet></router-outlet>
      </main>
    </div>
  `,
    styles: [`
    .layout-container {
      display: flex;
      min-height: 100vh;
    }

    .main-content {
      flex: 1;
      margin-left: 250px; /* Width of sidebar */
      padding: 2rem;
      background-color: var(--bg-secondary);
    }

    @media (max-width: 768px) {
      .main-content {
        margin-left: 0;
      }
      /* Mobile sidebar logic needed later */
    }
  `]
})
export class MainLayoutComponent { }
