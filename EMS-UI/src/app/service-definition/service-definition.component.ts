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
export class ServiceDefinitionComponent implements OnChanges {

  @Input() selectedSupplier: number;
  @Input() selectedServiceDefinition: string;

  cdssSupplierId: number;
  serviceDefinition: string;
  tempSelectedServiceDefinitionId: string;

  constructor(private serviceDefinitionService: ServiceDefinitionService) {}

  async ngOnChanges(changes: SimpleChanges) {
    if (changes.selectedSupplier !== undefined) {
      this.cdssSupplierId = changes.selectedSupplier.currentValue;
    }

    if (changes.selectedServiceDefinition !== undefined) {
      this.tempSelectedServiceDefinitionId = changes.selectedServiceDefinition.currentValue;
    }

    // If there's been a change to the supplier use the old service def selected.
    if (this.tempSelectedServiceDefinitionId !== undefined) {
      this.serviceDefinitionService
      .getServiceDefinition(this.cdssSupplierId, this.tempSelectedServiceDefinitionId)
          .subscribe(
            serviceDefinition => (this.serviceDefinition = serviceDefinition)
          );
    }
  }
}
