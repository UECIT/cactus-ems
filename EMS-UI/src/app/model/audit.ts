export class Interaction {
    interactionType: InteractionType;
    createdDate: number; //instant
    additionalProperties: Map<string, string> = new Map<string, string>();
}

export enum InteractionType {
    ENCOUNTER = "Encounter",
    SERVICE_SEARCH = "Service Search"
}