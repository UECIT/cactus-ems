import { Pipe, PipeTransform } from '@angular/core';
import { Age } from '../model/ageModel';

@Pipe({
  name: 'age'
})
export class AgePipe implements PipeTransform {
  transform(value: any) {
    return new Age(value).toString();
    // throw new Error('Method not implemented.');
  }
}
