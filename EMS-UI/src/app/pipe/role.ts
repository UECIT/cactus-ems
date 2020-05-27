import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'role'
})
export class RolePipe implements PipeTransform {
  transform(value: string) {
    if (value === 'ROLE_ADMIN') {
      return 'Admin';
    } else if (value === 'ROLE_SUPPLIER_ADMIN') {
      return 'Supplier Admin';
    } else if (value === 'ROLE_NHS') {
      return 'NHS User';
    } else if (value === 'ROLE_CDSS') {
      return 'CDSS Supplier';
    } else {return value; }
  }
}
