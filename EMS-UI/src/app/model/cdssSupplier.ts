export class CdssSupplier {
  id: number;
  name: string;
  serviceDefinitions: ServiceDefinition[];
}

export class ServiceDefinition {
  serviceDefinitionId: number;
  description: string;
}

export class NewCdssSupplier extends CdssSupplier {
  baseUrl: string;
  constructor() {
    super();
    this.id = 0;
    this.name = '';
    this.baseUrl = '';
    this.serviceDefinitions = [];
  }
}
