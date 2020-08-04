// `ng build ---prod` replaces this file with `environment.prod.ts`
// via config provided in angular.json
export const environment = {
  ENV_NAME: 'local',
  EMS_API: 'http://localhost:8083',
  UECDI_API: 'http://localhost:5000',
  UECDI_VALIDATE_API: 'http://localhost:7000/tkw-client',
  TERM_API: 'https://ontoserver.dataproducts.nhs.uk/fhir',
  USER_GUIDE: 'https://uec-connect-conformance-guide.netlify.app/'
};

/*
 * In development mode, to ignore zone related error stack frames such as
 * `zone.run`, `zoneDelegate.invokeTask` for easier debugging, you can
 * import the following file, but please comment it out in production mode
 * because it will have performance impact when throw error
 */
 import 'zone.js/dist/zone-error';  // Included with Angular CLI.
