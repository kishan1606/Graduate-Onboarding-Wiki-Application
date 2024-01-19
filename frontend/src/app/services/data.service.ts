import { Injectable } from '@angular/core';
import { User } from '../models/user-model';
import { Company } from '../models/company-model';
import * as CryptoJS from 'crypto-js';

@Injectable({
  providedIn: 'root',
})
export class DataService {
  activeUser: User | null = null;
  activeCompanyId: number | null = null;
  teamId: number | null = null;
  teamName: string | null = null;

  private readonly KEY = '2fCd8BhWkZeNgRsPmTq3t6w9z$C&F)J@'

  encrypt(data: any, credentials: any) {
    this.activeUser = {
      id: data.id,
      username: credentials.username,
      password: credentials.password,
      firstName: data.profile.firstName,
      lastName: data.profile.lastName,
      email: data.profile.email,
      active: data.active,
      admin: data.admin,
      status: data.status,
      companies: data.companies,
      teams: data.teams,
    }
    const encryptedData = CryptoJS.AES.encrypt(JSON.stringify(this.activeUser), this.KEY).toString();
    sessionStorage.setItem('user_data', encryptedData);
  }

  decrypt(): boolean {
    const encryptedData = sessionStorage.getItem('user_data');
    if (encryptedData) {
      const decryptedData = JSON.parse(CryptoJS.AES.decrypt(encryptedData, this.KEY).toString(CryptoJS.enc.Utf8));
      this.activeUser = decryptedData;
      return true;
    } else {
      return false;
    }
  }

  setActiveCompanyId(id: number) {
    // sessionStorage.setItem('activeCompanyId', id.toString())
    this.activeCompanyId = id;
  }
}
