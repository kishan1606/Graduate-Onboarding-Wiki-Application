import { Company } from './company-model';
import { Team } from './team-model';

export interface User {
  id: number;
  username: string;
  password: string;
  firstName: string;
  lastName: string;
  email: string;
  phoneNumber?: string;
  active: boolean;
  admin: boolean;
  status: String;
  companies?: Array<Company>;
  teams?: Array<Team>;
}
