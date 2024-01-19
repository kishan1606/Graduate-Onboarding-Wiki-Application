import { Time } from "@angular/common";

export interface Announcement {
    id: number,
    date: Date, //Time Object of { Hours: number, Minutes: number }
    title?: string,
    message: string,
    company: number, //The Company ID
    author: number // The User ID of creator
}
