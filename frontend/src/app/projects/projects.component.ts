import { Component, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { ModalComponent } from '../components/modals/modal/modal.component';
import { ApiCallsService } from '../services/api-calls.service';
import { DataService } from 'src/app/services/data.service';
import { Project } from '../models/project-model';
import { User } from '../models/user-model';

@Component({
  selector: 'app-projects',
  templateUrl: './projects.component.html',
  styleUrls: ['./projects.component.css'],
})
export class ProjectsComponent {
  @ViewChild(ModalComponent) modalComponent!: ModalComponent;
  modalType: string = 'create-edit-project';
  projectId: number | undefined;

  teamId = this.dataService.teamId;
  teamName = this.dataService.teamName;

  curUser: User | null = this.dataService.activeUser;
  companiesId: number | null = this.dataService.activeCompanyId;

  isAdmin: boolean | null = null;
  projects: Project[] = []

  constructor(private router: Router, private apiCallsService: ApiCallsService, private dataService: DataService) {}

  ngOnInit(): void {
    if (!this.dataService.decrypt()) {
      this.router.navigateByUrl("/login")
    } else {
      this.curUser = this.dataService.activeUser
    }
    if (!this.curUser) {
      this.router.navigateByUrl("/login")
    }
    this.isAdmin = this.curUser!.admin
    if (this.teamId) {
      this.getProjects(this.teamId, this.companiesId!);
    } else if (this.curUser && this.curUser.admin) {
      this.router.navigateByUrl("teams")
    } else if (this.curUser && this.curUser.teams) {
      this.teamId = this.curUser.teams[0].id
      if (this.companiesId) {
        this.getProjects(this.teamId, this.companiesId!);
      } else if (this.curUser && this.curUser.companies) {
        this.companiesId = this.curUser.companies[0].id;
        this.getProjects(this.teamId, this.companiesId!);
      } else {
        this.router.navigateByUrl("/login")
      }
    } else {
      this.router.navigateByUrl("/login")
    }
  }

  openNewModal() {
    this.projectId = undefined; // or null
    this.modalComponent.setProjectId(undefined);
    this.modalComponent.toggleModal();
  }

  openEditModal(projectId: number) {
    this.projectId = projectId;
    this.modalComponent.setProjectId(projectId);
    this.modalComponent.toggleModal();
  }

  getProjects = async (tId: number,cId: number) => {
    (await this.apiCallsService.getAllProjects(tId,cId)).subscribe((response) => {
      this.projects = response;
    });
  };

  refreshProjects(){
    this.getProjects(this.teamId!, this.companiesId!)
  }
}
