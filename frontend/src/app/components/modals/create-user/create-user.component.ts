import { Component, EventEmitter, Input, Output, ViewChild } from '@angular/core';
import { FormControl, FormGroup, Validators, ValidatorFn, AbstractControl, ValidationErrors, FormBuilder, EmailValidator } from '@angular/forms';
import { ApiCallsService } from 'src/app/services/api-calls.service';
import { User } from 'src/app/models/user-model';
import { DataService } from 'src/app/services/data.service';
import { ModalComponent } from '../modal/modal.component';
import { Company } from 'src/app/models/company-model';

@Component({
  selector: 'app-create-user',
  templateUrl: './create-user.component.html',
  styleUrls: ['./create-user.component.css']
})
export class CreateUserComponent {
  @ViewChild(ModalComponent) modalComponant!: ModalComponent;
  newUserForm: FormGroup = new FormGroup({
    username: new FormControl<string>('', [Validators.required]),
    first: new FormControl<string>('', [Validators.required]),
    last: new FormControl<string>('', [Validators.required]),
    email: new FormControl<string>('', [Validators.required]),
    phone: new FormControl<string>('', [Validators.required]),
    password: new FormControl<string>('', [Validators.required]),
    confirm: new FormControl<string>('', [Validators.required]),
    admin: new FormControl<boolean>(false, [Validators.required])
  })

  curCompanyId: number | null = this.dataService.activeCompanyId
  passMatch: boolean = true
  validUser: boolean = true
  validEmail: boolean = true
  newUserUsernameExists: boolean = true
  newUserPasswordExists: boolean = true
  newUserFirstExists: boolean = true
  newUserLastExists: boolean = true
  newUserEmailExists: boolean = true
  newUserPhoneExists: boolean = true
  newUserAdminExists: boolean = true
  @Output() addedUser = new EventEmitter<any>();

  curUser: User | null = this.dataService.activeUser

  constructor(private apiService: ApiCallsService, private dataService: DataService, private fb: FormBuilder) {}

  ngOnInit(): void {
  }

  async onSubmit() {
    this.passMatch = true
    this.validUser = true
    this.validEmail = true
    this.newUserUsernameExists = true
    this.newUserPasswordExists = true
    this.newUserFirstExists = true
    this.newUserLastExists = true
    this.newUserEmailExists = true
    this.newUserPhoneExists = true
    if(this.newUserForm.controls['password'].value !== this.newUserForm.controls['confirm'].value){
      this.passMatch = false
      return
    }
    this.passMatch = true

    // validate username and form values
    let userNameToCheck = this.newUserForm.controls['username'].value
    console.log("Checking Username: ", userNameToCheck)
    if(!userNameToCheck){
      this.newUserUsernameExists = false
      return
    }
    this.newUserUsernameExists = true
    ;(await this.apiService.validateUsernameAvailable(userNameToCheck)).subscribe(async (response) => {
      console.log("res", response)
      if(!response) {
        this.validUser = false
        console.log("Failed at response")
        return
      }
      if(!this.curCompanyId){
        console.log("Failed at company id")
        return
      }

      let newUserFirst = this.newUserForm.controls['first'].value
      if(!newUserFirst){
        this.newUserFirstExists = false
        console.log("Failed at first")
        return
      }
      let newUserLast = this.newUserForm.controls['last'].value
      if(!newUserLast){
        this.newUserLastExists = false
        console.log("Failed at last")
        return
      }
      let newUserUsername = this.newUserForm.controls['username'].value
      if(!newUserUsername){
        this.newUserUsernameExists = false
        console.log("Failed at username")
        return
      }
      let validEmail = String(this.newUserForm.controls['email'].value)
      .toLowerCase()
      .match(
        /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|.(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/
      );
      if(!validEmail){
        this.newUserEmailExists = false
        return
      }
      let newUserEmail = this.newUserForm.controls['email'].value
      this.newUserEmailExists = true
      let newUserPhone = this.newUserForm.controls['phone'].value
      if(!newUserPhone){
        this.newUserPhoneExists = false
        console.log("Failed at phone")
        return
      }
      let newUserPassword = this.newUserForm.controls['password'].value
      if(!newUserPassword){
        this.newUserPasswordExists = false
        console.log("Failed at password")
        return
      }

      let newUserAdmin = this.newUserForm.controls['admin'].value
      // toggle modal
      this.toggleModal()
      // post new user
      console.log("Admin Checkbox: ", this.newUserForm.controls['admin'].value)
      let newUser: User = {
        id: 1, // not submitted, but added to fit user model
        username: newUserUsername,
        password: newUserPassword,
        firstName: newUserFirst,
        lastName: newUserLast,
        email: newUserEmail,
        phoneNumber: newUserPhone,
        active: false,
        admin: newUserAdmin,
        status: ""
      };
      (await this.apiService.createUser(newUser, this.curCompanyId)).subscribe((response) => {
        this.addedUser.emit(response)
      })
    })
    

  }
  toggleModal() {
    const overlay = document.getElementById('overlay');
    const modal = document.getElementById('modal');

    if (overlay && modal) {
      if (overlay.style.display === 'block') {
        overlay.style.display = 'none';
        modal.style.display = 'none';
      } else {
        overlay.style.display = 'block';
        modal.style.display = 'block';
      }
    }
  }

}
