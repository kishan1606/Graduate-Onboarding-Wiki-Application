import { Component, Input } from '@angular/core';
import { Announcement } from 'src/app/models/announcement-model';
import { User } from 'src/app/models/user-model';
import { DataService } from 'src/app/services/data.service';

@Component({
  selector: 'app-announcement',
  templateUrl: './announcement.component.html',
  styleUrls: ['./announcement.component.css']
})
export class AnnouncementComponent {
  @Input() announcement!: Announcement;
  
  months: string[] = [
    "January", "February", "March", "April", "May", "June",
    "July", "August", "September", "October", "November", "December"
  ]
  constructor(private dataService: DataService) { }
  user!: User;
  date!: Date;

  ngOnInit(): void {
    this.user = this.dataService.activeUser!;
    this.date = new Date(this.announcement.date)
    console.log(this.announcement.author.profile.firstName);
    
  }
  // get user.first from service?
}