import { Company } from "./company-model";

export interface Team {
    id: number,
    name: String,
    description: String,
    company: number //The company ID
}