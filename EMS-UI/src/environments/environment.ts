// This file can be replaced during build by using the `fileReplacements` array.
// `ng build ---prod` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.

export const environment = {
  // production: true,
  EMS_API: 'http://localhost:8081',
  UECDI_API: 'http://localhost:5000',
  UECDI_VALIDATE_API: 'http://localhost:7000/tkw-client',

  // EMS_API: 'http://uecdi-tom-ems.eu-west-2.elasticbeanstalk.com:5000',
  // UECDI_API: 'http://uecdi-pci-20190401.eu-west-2.elasticbeanstalk.com:5000',
  // UECDI_VALIDATE_API: 'http://uecdi-tom-tkw.eu-west-2.elasticbeanstalk.com/tkw-client',

  TERM_API: 'https://ontoserver.dataproducts.nhs.uk/fhir',
  version: 'v1.0.6'
};

/*
 * In development mode, to ignore zone related error stack frames such as
 * `zone.run`, `zoneDelegate.invokeTask` for easier debugging, you can
 * import the following file, but please comment it out in production mode
 * because it will have performance impact when throw error
 */
// import 'zone.js/dist/zone-error';  // Included with Angular CLI.
