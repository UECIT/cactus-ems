import {
  Component,
  OnInit,
  Input,
  OnChanges,
  SimpleChanges
} from '@angular/core';
import { ServiceDefinitionService } from '../service/service-definition.service';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-service-definition',
  templateUrl: './service-definition.component.html',
  styleUrls: ['./service-definition.component.css']
})
export class ServiceDefinitionComponent implements OnInit, OnChanges {
  @Input() selectedSupplier: number;
  @Input() selectedServiceDefinition: string;
  @Input() selectedQueryType: string;

  CdssUrl: any;
  serviceDefinition: any;

  tempSelectedServiceDefinition: any;
  tempSelectedQueryType: any;

  constructor(private serviceDefinitionService: ServiceDefinitionService, private toastr: ToastrService) {}

  ngOnInit() {}

  async ngOnChanges(changes: SimpleChanges) {
    console.log(changes);

    if (changes.selectedSupplier !== undefined) {
      if (changes.selectedSupplier.currentValue !== undefined) {
        this.CdssUrl = await this.serviceDefinitionService.getCdssSupplierUrl(
          changes.selectedSupplier.currentValue
        ).catch(err => {
          this.toastr.error(
            err.error.target.__zone_symbol__xhrURL + ' - ' +
            err.message);
        });
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
    // &trigger-eventdata-id={{data_req_id}}
    if (this.tempSelectedQueryType.includes('attributes') && this.tempSelectedServiceDefinition !== undefined) {

    if (this.tempSelectedServiceDefinition === 107257) {
      this.serviceDefinitionService
      .getServiceDefinitionByQuery2(
        this.CdssUrl + 'ServiceDefinition?',
        'ACTIVE',
        false,
        '2017-04-12',
        '2020-04-12'
      )
      .subscribe(
        serviceDefinition => (this.serviceDefinition = serviceDefinition.entry[0])
      );
    } else {
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
        this.getTriggers(this.tempSelectedServiceDefinition)
      )
      .subscribe(
        serviceDefinition => (this.serviceDefinition = serviceDefinition.entry[0])
      );
    }
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
        return '4';
      case '2':
        return '15';
      case '3':
        return '24';
      case '4':
        return '28';
      case '5':
        return '31';
      case '6':
        return '23';
      case '7':
        return '37';
      case '8':
       return '46';
      case '9':
        return '49';
      case '10':
        return '51';
      case '11':
        return '4';
      case '12':
        return '15';
      case '13':
        return '24';
      case '14':
        return '28';
      default:
        break;
    }
  }
}
