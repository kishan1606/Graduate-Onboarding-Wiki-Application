
import { Component, OnInit, ViewChild } from '@angular/core';
import { ApiCallsService } from '../services/api-calls.service';
import { ModalComponent } from "../components/modals/modal/modal.component";
import { Router } from '@angular/router';
import { DataService } from 'src/app/services/data.service';


@Component({
  selector: 'app-teams',
  templateUrl: './teams.component.html',
  styleUrls: ['./teams.component.css']
})
export class TeamsComponent {
  teams: any[] = [];
  CompanyID: number | null = this.dataService.activeCompanyId;

  modalType: string = 'create-team';
  @ViewChild(ModalComponent) modalComponent!: ModalComponent;

  openNewTeamModal(): void { }

  constructor(private router: Router, private apiCallsService: ApiCallsService, private dataService: DataService) { }

  ngOnInit() {
    if (!this.dataService.decrypt()) {
      this.router.navigateByUrl("/login")
    }
    let curUser = this.dataService.activeUser
    if (!curUser || !curUser.admin) {
      this.router.navigateByUrl("/login")
    }
    this.CompanyID = this.dataService.activeCompanyId

    if (this.CompanyID) {
      this.apiCallsService.getTeamsByCompanyId(this.CompanyID).subscribe(
        (teamsData) => {
          this.teams = teamsData;
          this.teams.forEach((team) => {
            this.apiCallsService.getProjectsByTeamId(this.CompanyID!, team.id).subscribe(
              (projects) => {
                team.projectsCount = projects.length;
              },
              (error) => {
                console.error('Error fetching projects for team', team.id, error);
              }
            );
          });
        },
      );
    } else if (curUser && curUser.admin) {
      this.router.navigateByUrl("select-company")
    } else if (curUser && curUser.companies) {
      this.CompanyID = curUser.companies[0].id;
      this.apiCallsService.getTeamsByCompanyId(this.CompanyID).subscribe(
        (teamsData) => {
          this.teams = teamsData;
          this.teams.forEach((team) => {
            this.apiCallsService.getProjectsByTeamId(this.CompanyID!, team.id).subscribe(
              (projects) => {
                team.projectsCount = projects.length;
              },
              (error) => {
                console.error('Error fetching projects for team', team.id, error);
              }
            );
          });
        },
      );
    } else {
      this.router.navigateByUrl("/login")
    }
  }

  toggleModal() {
    this.modalComponent.toggleModal()
  }

  checkChildForData($event: any) {
    let addingTeam: any = {
      id: $event.id,
      name: $event.name,
      description: $event.description,
      company: this.CompanyID, //The company ID
    }

    this.teams.push(addingTeam)
    this.toggleModal()
  }
}
