// This file can be replaced during build by using the `fileReplacements` array.
// `ng build ---prod` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.

export const environment = {
  production: false,
  EMS_API: 'http://uecdi-tom-ems.eu-west-2.elasticbeanstalk.com:5000',
  // EMS_API: 'http://localhost:8081',
  TERM_API: 'https://ontoserver.dataproducts.nhs.uk/fhir',
  UECDI_API: 'http://uecdi-pci-20190401.eu-west-2.elasticbeanstalk.com:5000',
  version: 'v1.0.4'
};

/*
 * In development mode, to ignore zone related error stack frames such as
 * `zone.run`, `zoneDelegate.invokeTask` for easier debugging, you can
 * import the following file, but please comment it out in production mode
 * because it will have performance impact when throw error
 */
// import 'zone.js/dist/zone-error';  // Included with Angular CLI.
