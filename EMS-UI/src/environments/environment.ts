// This file can be replaced during build by using the `fileReplacements` array.
// `ng build ---prod` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.

export const environment = {
  production: false,
  EMS_API: 'http://localhost:8083',
  UECDI_API: 'http://localhost:5000',
  UECDI_VALIDATE_API: 'http://localhost:7000/tkw-client',
  TERM_API: 'https://ontoserver.dataproducts.nhs.uk/fhir',
  version: 'v1.0.6'
};

/*
 * In development mode, to ignore zone related error stack frames such as
 * `zone.run`, `zoneDelegate.invokeTask` for easier debugging, you can
 * import the following file, but please comment it out in production mode
 * because it will have performance impact when throw error
 */
 import 'zone.js/dist/zone-error';  // Included with Angular CLI.
