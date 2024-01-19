import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, map, throwError } from 'rxjs';
import { User } from '../models/user-model';
import { DataService } from './data.service';
import { Announcement } from '../models/announcement-model';
import {Team} from "../models/team-model";
import { Project } from '../models/project-model';



@Injectable({
  providedIn: 'root',
})
export class ApiCallsService {
  private apiUrl = 'http://localhost:8080';

  constructor(private http: HttpClient, private dataService: DataService) { }

  testUser1: User = {
    id: 1,
    username: 'TestUser',
    password: 'pass',
    firstName: 'Test',
    lastName: 'User',
    email: 'test@user.com',
    active: true,
    admin: true, //Change this value to change login page for User
    status: 'Joined',
    companies: [],
    teams: [],
  };

  /**
   * Log-In API Call
   * Returns promise while it waits on the confirmation from the BE
   */
  async login(username: string, password: string) {
    /** make backend api call for the user */
    let requestUrl: string = this.apiUrl + '/users/login';
    let requestBody: Object = { username, password };

    return this.http.post<any>(requestUrl, requestBody).pipe(
      map((response) => {
        //Create user Object based on response data
        const authenticatingUser: User = {
          id: response.id,
          username: username,
          password: password,
          firstName: response.profile.firstName,
          lastName: response.profile.lastName,
          email: response.profile.email,
          active: response.active,
          admin: response.admin,
          status: response.status,
          companies: response.companies,
          teams: response.teams,
        };

        // Make an Array of User Team Id's
        let userTeams: any[] = response.teams.map((team: any) => {
          return team;
        });

        // Use Session Storage for User object, team id's, and company id

        this.dataService.activeCompanyId = response.companies[0].id
        this.dataService.activeUser = authenticatingUser;
        if(userTeams[0]) {
          this.dataService.teamId = userTeams[0].id
          this.dataService.teamName = userTeams[0].name
        }
        this.dataService.encrypt(response, requestBody);
        return authenticatingUser;
      }),
      catchError((error) => {
        if (error.status === 404) {
          // Handle the "user not found" error here, e.g., show a message to the user
          console.error('User not found:', error);
        } else {
          // Handle other errors, such as network issues or server errors
          console.error('Login error:', error);
        }

        // Rethrow the error or return a default value as needed
        return throwError(error);
      })
    );
  }

  async getCompanyUsers(companyId: number) {
    let requestUrl: string = this.apiUrl + '/companies/' + companyId + '/users';
    return this.http.get<any>(requestUrl).pipe(
      map((response) => {
        console.log('Response:', response);
        let responseUsers: Array<any> = response;
        let users: Array<User> = [];
        for (let i = 0; i < responseUsers.length; i++) {
          let newUser: User = {
            id: responseUsers[i].id,
            username: responseUsers[i].profile.username,
            password: responseUsers[i].profile.password,
            firstName: responseUsers[i].profile.firstName,
            lastName: responseUsers[i].profile.lastName,
            email: responseUsers[i].profile.email,
            phoneNumber: responseUsers[i].profile.phone,
            active: responseUsers[i].active,
            admin: responseUsers[i].admin,
            status: responseUsers[i].status,
            companies: responseUsers[i].companies,
            teams: responseUsers[i].teams,
          };
          users.push(newUser);
        }
        return users;
      }),
      catchError((error) => {
        if (error.status === 404) {
          // Handle the "user not found" error here, e.g., show a message to the user
          console.error('Users not found:', error);
        }
        // Rethrow the error or return a default value as needed
        return throwError(error);
      })
    );
  }

  async getAnnouncements(id: number) {
    let requestUrl: string = this.apiUrl + `/companies/${id}/announcements`;
    return this.http.get<Announcement[]>(requestUrl);
  }

  async createAnnouncement(id: number, requestBody: Object) {
    // let requestUrl: string = this.apiUrl + `/companies/${id}/announcements`;
    let requestUrl: string = this.apiUrl + `/announcements/${id}`;
    return this.http.post<Announcement>(requestUrl, requestBody);
  }

  async getAllProjects(tId: number, cId: number) {
    console.log("CompanyId for projects: ", tId)
    console.log("TeamId for projects: ", tId)
    let requestUrl: string = this.apiUrl + `/companies/${cId}/teams/${tId}/projects`;
    return this.http.get<Project[]>(requestUrl);
  }

  async getProjectById(id: number) {
    let requestUrl: string = this.apiUrl + `/projects/${id}`;
    return this.http.get<Project[]>(requestUrl);
  }

  async createProject(teamId: number, requestBody: Object) {
    let requestUrl: string = this.apiUrl + `/teams/${teamId}/projects`;
    return this.http.post<Project>(requestUrl, requestBody);
  }

  async editProject(projectId: number, requestBody: Object) {
    let requestUrl: string = this.apiUrl + `/projects/${projectId}`;
    return this.http.patch<Project>(requestUrl, requestBody);
  }

  getTeamsByCompanyId(companyId: number): Observable<Team[]> {
    let requestUrl: string = `${this.apiUrl}/companies/${companyId}/teams`;
    return this.http.get<Team[]>(requestUrl)
  }

  getProjectsByTeamId(companyId: number, teamId: number): Observable<any[]> {
    const url = `${this.apiUrl}/companies/${companyId}/teams/${teamId}/projects`;
    return this.http.get<any[]>(url);
  }

  async validateUsernameAvailable(username: string) {
    let requestUrl: string = this.apiUrl + `/validate/username/available/${username}`;
    return this.http.get<boolean>(requestUrl)
  }

  async createUser(user: User, companyId: number) {
    console.log("Creating user")
    console.log(user)
    let requestBody = {
      credentials: {
        username: user.username,
        password: user.password
      },
      profile: {
        firstName: user.firstName,
        lastName: user.lastName,
        email: user.email,
        phone: user.phoneNumber
      },
      companyId: companyId,
      admin: user.admin,
    }
    console.log(requestBody)
    let requestUrl: string = this.apiUrl + `/users`;
    return this.http.post<Object>(requestUrl, requestBody);
  }

  getTeamsByUserId(userId: number): Observable<Team[]> {
    let requestUrl: string = `${this.apiUrl}/users/${userId}/teams`;
    return this.http.get<Team[]>(requestUrl)
  }

  getUsersFirstNameAndId(companyId: number): Observable<{ id: number, firstName: string }[]> {
    let requestUrl: string = `${this.apiUrl}/companies/${companyId}/users`;
    return this.http.get<any[]>(requestUrl).pipe(
      map(response => response.map(user => ({
        id: user.id,
        firstName: user.profile.firstName
      }))),
      catchError(error => {
        console.error('Error fetching user data:', error);
        return throwError(() => new Error('Error fetching user data'));
      })
    );
  }

  createTeam(companyId: number, teamData: any): Observable<any> {
    const requestUrl: string = `${this.apiUrl}/companies/${companyId}/teams`;
    return this.http.post<any>(requestUrl, teamData);
  }
}
