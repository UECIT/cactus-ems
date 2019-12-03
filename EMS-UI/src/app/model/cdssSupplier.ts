export class CdssSupplier {
  id: number;
  name: string;
  baseUrl: string;
  serviceDefinitions: ServiceDefinition[];
  referencingType: ReferencingType;
}

export type ReferencingType = 'ServerReferences' | 'ContainedReferences' | 'BundledReferences';

export class ReferencingTypes {
  static readonly server: ReferencingType = 'ServerReferences';
  static readonly contained: ReferencingType = 'ContainedReferences';
  static readonly bundled: ReferencingType = 'BundledReferences';
  static toOrdinal(type: ReferencingType) {
    return [ReferencingTypes.server, ReferencingTypes.contained, ReferencingTypes.bundled]
      .indexOf(type);
  }
}

export class ServiceDefinition {
  serviceDefinitionId: string;
  description: string;
}

export class NewCdssSupplier extends CdssSupplier {
  baseUrl: string;
  constructor() {
    super();
    this.id = 0;
    this.name = '';
    this.baseUrl = '';
    this.referencingType = ReferencingTypes.contained;
    this.serviceDefinitions = [];
  }
}
