import { Component, OnInit } from '@angular/core';
import { MenuItem } from 'primeng/api';
import { Router } from '@angular/router';
import { AuthService } from 'src/app/data/service/auth-service/auth.service';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss'],
})
export class NavbarComponent implements OnInit {
  items: MenuItem[] = [];
  avtarMenuItems: MenuItem[] = [];

  constructor(private authService: AuthService, private router: Router) {}

  ngOnInit() {
    this.items = [
      {
        label: 'Home',
        routerLink: '/',
      },      
      {
        label: 'Blog',
        icon: 'pi pi-fw pi-book',
        items: [
          {
            label: 'Blog Feed',
            icon: 'pi pi-fw pi-list',
            routerLink: '/blog',
          },
          {
            label: 'Add New',
            icon: 'pi pi-fw pi-plus',
            routerLink: '/blog/blog-editor',
          },
          {
            label: 'Your Blogs',
            icon: 'pi pi-fw pi-user-edit',
            routerLink: '/blog/user-blogs',
          },
        ],
      },
      {
        label: 'Your Bucket List',
        icon: 'pi pi-fw pi-map',
        routerLink: '/user-profile/bucket-list',
      },
      {
        label: 'Your Future Trips',
        icon: 'pi pi-car',
        routerLink: '/future-trip',
      },
      {
        label: 'About Us',
        icon: 'pi pi-users',
        routerLink: '/about-us',
      },
    ];
    this.avtarMenuItems = [
      {
        label: 'Profile',
        routerLink: '/user-profile',
        icon: 'pi pi-id-card',
      },
      {
        label: 'Logout',
        icon: 'pi pi-sign-out',
        command: (event) => {
          this.authService.logout();
        },
      },
    ];
  }
}
