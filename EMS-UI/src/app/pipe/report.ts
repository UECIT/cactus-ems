import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'report'
})
export class ReportPipe implements PipeTransform {
  transform(value: string) {
    if (value === 'AMBULANCE') {
      return 'Integrated Urgent Care Ambulance Request:';
    } else if (value === 'DOS') {
      return 'Directory of Service:';
    } else if (value === 'ONE_ONE_ONE_V2') {
      return 'Version 2 Integrated Urgent Care Report:';
    } else if (value === 'ONE_ONE_ONE_V3') {
      return 'Version 3 Integrated Urgent Care Report:';
    } else {return value; }
  }
}
