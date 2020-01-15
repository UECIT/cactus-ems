export class HealthcareService {
    id: number;
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