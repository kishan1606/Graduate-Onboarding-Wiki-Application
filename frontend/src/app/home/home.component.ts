import { Component, OnInit, ViewChild } from '@angular/core';
import { ModalComponent } from '../components/modals/modal/modal.component';
import { User } from '../models/user-model';
import { Router } from '@angular/router';
import { ApiCallsService } from '../services/api-calls.service';
import { DataService } from 'src/app/services/data.service';
import { Announcement } from '../models/announcement-model';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})


export class HomeComponent implements OnInit {
  @ViewChild(ModalComponent) modalComponent!: ModalComponent;

  user: User | null = this.dataService.activeUser;
  companyId: number | null = this.dataService.activeCompanyId;

  modalType: string = 'create-announcement';
  announcements: Announcement[] = [];
  
  constructor(private apiCallsService: ApiCallsService, private router: Router, private dataService: DataService) { }

  ngOnInit(): void {
    // authorize
    if (this.dataService.decrypt()) {
      console.log("decryption successful")
      this.user = this.dataService.activeUser;
    } else {
      this.router.navigateByUrl("login");
    }

    if (!this.user) {
      this.router.navigateByUrl("login");
    }
    if (this.companyId) {
      this.getAnnouncements(this.companyId!);
    } else if (this.user && this.user.admin) {
      this.router.navigateByUrl("select-company")
    } else if (this.user && this.user.companies) {
      this.companyId = this.user.companies[0].id;
      this.getAnnouncements(this.companyId!);
    } else {
      this.router.navigateByUrl("login")
    }
  }

  getAnnouncements = async (id: number) => {
    (await this.apiCallsService.getAnnouncements(id)).subscribe((response) => {
      this.announcements = response;
      // console.log(this.announcements);
      
    });
  }

  toggleModal() {
    this.modalComponent.toggleModal();
  }
}
