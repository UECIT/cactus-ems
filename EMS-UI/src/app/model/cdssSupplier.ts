import { SupplierInstance } from "./supplierInstance";

export class CdssSupplier extends SupplierInstance{
  serviceDefinitions: ServiceDefinition[];
  inputParamsRefType: ResourceReferenceType;
  inputDataRefType: ResourceReferenceType;
  supportedVersion: string;
  authToken: string;

  constructor() {
    super()
    this.id = 0;
    this.name = '';
    this.baseUrl = '';
    this.serviceDefinitions = [];
    this.inputDataRefType = ResourceReferenceType.ByReference;
    this.inputParamsRefType = ResourceReferenceType.ByReference;
    this.supportedVersion = '';
    this.authToken = '';
  }
}

export enum ResourceReferenceType {
  ByReference = 0,
  ByResource
}

export class ServiceDefinition {
  serviceDefinitionId: string;
  description: string;
}
