import { Component } from '@angular/core';
import { DataService } from '../services/data.service';
import { Router } from '@angular/router';
import { Company } from '../models/company-model';
import { User } from '../models/user-model';
import { ApiCallsService } from '../services/api-calls.service';

@Component({
  selector: 'app-select-company',
  templateUrl: './select-company.component.html',
  styleUrls: ['./select-company.component.css'],
})
export class SelectCompanyComponent {
  constructor(
    private api: ApiCallsService,
    private dataService: DataService,
    private router: Router
  ) {}

  ngOnInit(): void {
    if (!this.dataService.activeUser || !this.dataService.activeUser?.admin) {
      this.router.navigateByUrl('/login');
    }
  }

  activeUser: User | null = this.dataService.activeUser;

  companies: Array<Company> | undefined =
    this.dataService.activeUser?.companies;

  handleSelectCompany = (company: Company) => {
    // this.dataService.activeCompanyId = company.id;
    this.dataService.setActiveCompanyId(company.id);
    this.router.navigateByUrl('/home');
  };
}
