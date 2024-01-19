import { Component, EventEmitter, Input, Output, ViewChild } from '@angular/core';
import { ModalComponent } from 'src/app/components/modals/modal/modal.component';
import { DataService } from 'src/app/services/data.service';

@Component({
  selector: 'app-project',
  templateUrl: './project.component.html',
  styleUrls: ['./project.component.css']
})
export class ProjectComponent {

  @ViewChild(ModalComponent) modalComponent!: ModalComponent;
  isAdmin: boolean = this.dataService.activeUser!.admin;
  modalType: string = 'create-edit-project';
  @Input() project: any;
  // editData: any = null;
  constructor(private dataService: DataService) {}
  @Output() editClick: EventEmitter<any> = new EventEmitter<any>();
  openEditModal(projectId: any) {
    console.log(projectId)
    this.editClick.emit(projectId);
  }

  getTimeDifference(timestamp: string): string { //calculate time difference to show last updated time
    const currentTimestamp = new Date().getTime();
    const providedTimestamp = new Date(timestamp).getTime();
    const difference = currentTimestamp - providedTimestamp;
  
    const seconds = Math.floor(difference / 1000);
    const minutes = Math.floor(seconds / 60);
    const hours = Math.floor(minutes / 60);
    const days = Math.floor(hours / 24);

    if (days > 0) {
      return `last updated ${days} ${days === 1 ? 'day' : 'days'} ago`;
    } else if (hours > 0) {
      return `last updated ${hours} ${hours === 1 ? 'hour' : 'hours'} ago`;
    } else if (minutes > 0) {
      return `last updated ${minutes} ${minutes === 1 ? 'minute' : 'minutes'} ago`;
    } else {
      return `last updated ${seconds} ${seconds === 1 ? 'second' : 'seconds'} ago`;
    }
  }  
  // checkStatus(status: boolean): string  {
  //   return status ? 'active' : 'inactive';
  // }

  // toggleModal(projectId: any) {
  //   // console.log("edit modal with ID: ",this.project.id);
  //   // this.modalComponent.toggleModal();
  //   this.editData = projectId; // Store the project ID in editData
  //   console.log("edit modal with ID: ",projectId);
  //   this.modalComponent.setProjectId(projectId);
  //   this.modalComponent.toggleModal();
  // }
}