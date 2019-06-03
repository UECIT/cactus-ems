import {
  Component,
  OnInit,
  Input,
  OnChanges,
  SimpleChanges
} from '@angular/core';
import { CdssSupplier } from '../model/cdssSupplier';
import { ServiceDefinitionService } from '../service/service-definition.service';

@Component({
  selector: 'app-service-definition',
  templateUrl: './service-definition.component.html',
  styleUrls: ['./service-definition.component.css']
})
export class ServiceDefinitionComponent implements OnInit, OnChanges {
  @Input() selectedSupplier: CdssSupplier;
  @Input() selectedServiceDefinition: String;
  @Input() selectedQueryType: String;

  CdssUrl: any;
  serviceDefinition: any;

  tempSelectedServiceDefinition: any;
  tempSelectedQueryType: any;

  constructor(private serviceDefinitionService: ServiceDefinitionService) {}

  ngOnInit() {}

  async ngOnChanges(changes: SimpleChanges) {
    console.log(changes);

    if (changes.selectedSupplier !== undefined) {
      if (changes.selectedSupplier.currentValue !== undefined) {
        this.CdssUrl = await this.serviceDefinitionService.getCdssSupplierUrl(
          changes.selectedSupplier.currentValue.id
        );
      }
    }

    if (changes.selectedServiceDefinition !== undefined) {
      if (changes.selectedServiceDefinition.currentValue !== undefined) {
        this.tempSelectedServiceDefinition = changes.selectedServiceDefinition.currentValue;
      }
    }

    if (changes.selectedQueryType !== undefined) {
      if (changes.selectedQueryType.currentValue !== undefined) {
        this.tempSelectedQueryType = changes.selectedQueryType.currentValue;
      }
    }

    if (this.tempSelectedQueryType.includes('id') && this.tempSelectedServiceDefinition !== undefined) {
      this.serviceDefinitionService
      .getServiceDefinition(
        this.CdssUrl +
          'ServiceDefinition/' +
          this.tempSelectedServiceDefinition)
          .subscribe(
            serviceDefinition => (this.serviceDefinition = serviceDefinition)
          );
    }

    // {{BASE_URL}}
    // status=active
    // &experimental=false
    // &effective=ge{{TODAY}}
    // &effective=le{{TODAY}}
    // &useContext-code=gender  NEW
    // &useContext-valueconcept=http://hl7.org/fhir/administrative-gender|female NEW
    // &jurisdiction=urn:iso:std:iso:3166|ENG
    // &trigger-type=data-added
    // &trigger-eventdata-type=Observation
    // &trigger-eventdata-profile=[profile name]
    // &trigger-eventdata-valuecoding=[code]
    if (this.tempSelectedQueryType.includes('attributes') && this.tempSelectedServiceDefinition !== undefined) {
        this.serviceDefinitionService
        .getServiceDefinitionByQuery(
          this.CdssUrl + 'ServiceDefinition?',
          'ACTIVE',
          false,
          '2019-04-12',
          '2019-04-12',
          'party',
          this.getPartyCode(this.tempSelectedServiceDefinition),
          'ENG',
          'data-added',
          'Observation',
          'https://www.hl7.org/fhir/triggerdefinition.html',
          this.getTriggers(this.tempSelectedServiceDefinition)
        )
        .subscribe(
          serviceDefinition => (this.serviceDefinition = serviceDefinition.entry[0])
        );
    }
  }

  getPartyCode(serviceDefinitionId) {
    switch (serviceDefinitionId) {
      case '1':
        return '3';
      case '2':
        return '1';
      case '3':
        return '1';
      case '4':
        return '1';
      case '5':
        return '1';
      case '6':
        return '1';
      case '7':
        return '1';
      case '8':
       return '1';
      case '9':
        return '1';
      case '10':
        return '1';
      case '11':
        return '3';
      case '12':
        return '1';
      case '13':
        return '1';
      case '14':
        return '1';
      default:
        break;
    }
  }

  getSkillsetCode(serviceDefinitionId) {
    switch (serviceDefinitionId) {
      case '1':
        return '111CH';
      case '2':
        return '111CH';
      case '3':
        return '111CH';
      case '4':
        return '111CH';
      case '5':
        return '111CH';
      case '6':
        return '111CH';
      case '7':
        return '111CL';
      case '8':
       return '111CL';
      case '9':
        return '111CL';
      case '10':
        return '111CL';
      case '11':
        return '111CH';
      case '12':
        return '111CH';
      case '13':
        return '111CH';
      case '14':
        return '111CH';
      default:
        break;
    }
  }

  getTriggers(serviceDefinitionId) {
    switch (serviceDefinitionId) {
      case '1':
        return '240091000000105';
      case '2':
        return '271594007';
      case '3':
        return '250087009';
      case '4':
        return '309521004';
      case '5':
        return '45326000';
      case '6':
        return '57676002';
      case '7':
        return '248062006';
      case '8':
       return '288959006';
      case '9':
        return '35489007';
      case '10':
        return '49727002';
      case '11':
        return '240091000000105';
      case '12':
        return '271594007';
      case '13':
        return '250087009';
      case '14':
        return '309521004';
      default:
        break;
    }
  }
}
