export class Interaction {
    requestId: string;
    interactionType: InteractionType;
    createdDate: number; //instant
    additionalProperties: Map<string, string> = new Map<string, string>();
}

export enum InteractionType {
    ENCOUNTER = "Encounter",
    SERVICE_SEARCH = "Service Search"
}

export class ValidationRequest {
    instanceBaseUrl: string;
    type: InteractionType;
    searchAuditId: string // for service search
    caseId: string; //for encounters
}