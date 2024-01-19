import { Component, Input, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { DataService } from 'src/app/services/data.service';


@Component({
  selector: 'app-team',
  templateUrl: './team.component.html',
  styleUrls: ['./team.component.css']
})
export class TeamComponent implements OnInit {
  @Input() teamData: any;

  members: string[] = [];
  constructor(private router: Router, private dataService: DataService) {}

  ngOnInit(): void {
    if (this.teamData && this.teamData.description) { // This is ensuring whitespace and empty names are omitted.
      const separators = [',', '&'];
      const regex = new RegExp(separators.join('|'), 'g');
      this.members = this.teamData.description.split(regex)
        .map((s: string) => s.trim())
        .filter((name: string) => name !== "");
    }
  }

  handleClick() {
    console.log("Click")
    console.log(this.teamData.id)
    this.dataService.teamId = this.teamData.id
    this.dataService.teamName = this.teamData.name
    this.router.navigateByUrl("projects")
  }

}
