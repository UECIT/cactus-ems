export class SupplierInstance {
    name: string;
    baseUrl: string;
}

export class EmsSupplier extends SupplierInstance{
    id: number;
    authToken: string;
}