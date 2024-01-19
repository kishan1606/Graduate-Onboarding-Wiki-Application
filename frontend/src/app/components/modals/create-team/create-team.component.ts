import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {ApiCallsService} from '../../../services/api-calls.service';
import {DataService} from 'src/app/services/data.service';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {User} from "../../../models/user-model";

@Component({
  selector: 'app-create-team',
  templateUrl: './create-team.component.html',
  styleUrls: ['./create-team.component.css']
})
export class CreateTeamComponent implements OnInit {
  teamForm: FormGroup = new FormGroup({
    name: new FormControl<string>('', [Validators.required]),
    description: new FormControl<string>('', [Validators.required]),
  })
  selectedMembers: any[] = []
  users: { id: number, firstName: string }[] = [];
  selectedUserId: number | undefined;
  selectedMembersNames: any[] = []
  selectedValue: string = 'default'
  @Output() addedTeam = new EventEmitter<any>();

  constructor(private apiCallsService: ApiCallsService, private dataService: DataService) {}

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    const companyId: any = this.dataService.activeCompanyId;
    this.apiCallsService.getUsersFirstNameAndId(companyId).subscribe({
      next: (users: { id: number; firstName: string; }[]) => {
        this.users = users;
        console.log(users);
      },
      error: (error: any) => {
        console.error('Error fetching users:', error);
      }
    });
  }

  getUserFirstName(userId: number): string | undefined {
    const selectedUser = this.users.find(user => user.id === userId);
    return selectedUser?.firstName;
  }

  onSelect(event: any): void {
    const toCheck = this.selectedMembers.some(object => object.id === event.target.value)
    if (!toCheck){
      this.selectedMembers.push({
        id: event.target.value
      })
      let newMember: any = this.users.find(user => user.id == event.target.value)
      this.selectedMembersNames.push({
        name: newMember.firstName
      })
    }
  }

  removeSelectedMember(name: string): void {
    let toRemove: any = this.users.find(member => member.firstName === name)
    let filteredList = this.selectedMembers.filter(user => user.id != toRemove.id)
    let updatedNames = this.selectedMembersNames.filter(user => user.name != name)
    this.selectedMembersNames = updatedNames
    this.selectedMembers = filteredList
  }

  onSubmit(): void {
    let user: User = this.dataService.activeUser! 
    let users: string = ""

    this.selectedMembersNames.forEach((member: any) => {
      return users += (member.name + ',')
    })

    let requestBody: Object = {
      name: this.teamForm.controls['name'].value,
      description: users,
      teammates: this.selectedMembers,
      credentialsDto: {username: user.username, password: user.password}
    }
    console.log(requestBody)
    const companyId: any = this.dataService.activeCompanyId;
    this.apiCallsService.createTeam(companyId, requestBody).subscribe({
      next: (response: any) => {
        console.log('Team created successfully:', response);
        this.addedTeam.emit(response)
      },
      error: (error: any) => {
        console.error('Error creating team:', error);
      }
    });
  }
}
