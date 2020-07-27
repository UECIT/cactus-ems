import {Moment} from "moment";

export class Interaction {
    type: InteractionType;
    interactionId: string;
    startedAt: Moment;
}

export type InteractionType =
    "Encounter"
    | "Service Search"
    | "Is Valid"
    | "Check Services"
    | "Encounter Report"
    | "Handover";

export class ValidationRequest {
    instanceBaseUrl: string;
    type: InteractionType;
    interactionId: string;
}