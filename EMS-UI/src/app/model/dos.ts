export class HealthcareService {
    id: string;
    name: string;
    endpoint: string;
    description: string;
    active: boolean;
    appointmentRequired: boolean;
    phoneNumber: string;
    email: string;
    provision: string[];
    availableTimes: string[];
    notAvailableTimes: string[];
}