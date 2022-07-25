import { SafeHtml } from "@angular/platform-browser";
import { Pin } from "./pin";
import { UserProfile } from "./user-profile";

// model class for future trip
export interface FutureTrip {
    tripId: Number,
    tripName?: string,
    tripDescription: string,
    tripDate: string,
    pin?: Pin,
    user: UserProfile,
    htmlDescription?: SafeHtml
}