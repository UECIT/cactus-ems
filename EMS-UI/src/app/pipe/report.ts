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
    } else if (value === 'ONE_ONE_ONE') {
      return 'Integrated Urgent Care Report:';
    } else {return value; }
  }
}
