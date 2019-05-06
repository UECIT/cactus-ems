import { CdssSupplier } from './cdssSupplier';

export class User {
  username: string;
  name: string;
  enabled: boolean;
  role: string;
  cdssSuppliers: CdssSupplier[];
}

export class NewUser extends User {
  password: string;
  constructor() {
    super();
    this.name = '';
    this.username = '';
    this.password = '';
    this.enabled = true;
    this.role = '';
    this.cdssSuppliers = [];
  }
}
