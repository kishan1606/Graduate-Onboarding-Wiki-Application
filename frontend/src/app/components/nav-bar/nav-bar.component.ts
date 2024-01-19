import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { DataService } from 'src/app/services/data.service';

@Component({
  selector: 'app-nav-bar',
  templateUrl: './nav-bar.component.html',
  styleUrls: ['./nav-bar.component.css'],
})
export class NavBarComponent {
  constructor(router: Router, private dataService: DataService) {
    this.router = router;
  }

  router: Router | null = null;
  admin = this.dataService.activeUser?.admin;

  handleLogout = () => {
    this.dataService.activeCompanyId = null;
    this.dataService.activeUser = null;
    this.dataService.teamId = null;
    this.dataService.teamName = null;
    this.router?.navigateByUrl('login');
  };
}
