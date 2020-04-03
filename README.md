# EMS Test Harness

## Overview

This service implements an Encounter Management System, a system used for workflow management and to record, manage and track a patient’s episode of care through UEC settings. 

The EMS is responsible for invoking the decision support process on the CDSS. The EMS will typically also manage elements like user authentication, workflow and user interactions.

This proof of concept implementation is compliant with both v1.1 and v2.0 of the CDS API Spec and supports:

- User Login/Admin
- Triage Scenario Setup (Patient selection/search, context, user type, jurisdication)
- Receiving Encounter Reports to initiate triage.
- Service Definition Selection
- Full triage encounters by invoking the CDSS $evaluate operation and displaying the questionnaires, care plans and referral requests generated
- Service Redirects
- Invocation of the Directory of Service's $check-services operation. Allows the user to select a returned healthcare service which is added to the Referral Request.
- Handover to end-points specified by the returned healthcare service.
- Registering new CDSS Suppliers and Service Definitions
- Registering new EMS Suppliers
- Viewing audit logs
- Error handling

## Source Code Location

The repo for this project is located in a public GitLab space here: https://gitlab.com/ems-test-harness/cdss-ems

## Usage

### Prerequisites
Make sure you have everything installed as per the setup guide:
- Maven
- Angular CLI
- Docker
- MySQL 5
- Microsoft Developer Tools (Windows Only)
- NPM
- IntelliJ IDE (Recommended)

### Build Steps
The EMS has two parts:

#### Java Back End
This project is configured to run on port 8083. For local machines, this can be accessed at http://localhost:8083

This can be done either through the `docker-compose.yml` file:

`docker-compose up`

This will pull both the docker containers for both the database and the app itself.

If you want to run the app and the database separately you can use your own MySQL server (or start docker container cdss-ems_mysql_1 by itself) and then use the maven task:

`mvn spring-boot:run`

This is usually easier for development/debugging purposes. By default, logs are formatted with the full JSON context, but you can optionally add a spring profile to the maven task for cleaner logging:

- `-Dspring.profiles.active=dev` will output a simple 'TIME THREAD LEVEL MESSAGE' format
- `-Dspring.profiles.active=prettylogs` will output the JSON logs in an easier to read format.

#### Angular UI
In the EMS-UI directory you can run using:

`npm install` 

to install the dependencies and then

`ng serve --open` 

Then you can log in using the credentials:

- username: `admin`
- password: `admin@123`

The EMS will be accessed through it's angular UI. For local machines, this can be found at http://localhost:4200.

## Project Structure
### Backend
The EMS Backend is a Java Spring Application. It is split into three major layers:

- Controllers & Resource Providers - These contain end points for the EMS back end as well as FHIR end points for various resources that the EMS currently stores such as the Patient and the Encounter.
- Service Layer - This contains business functionality for how the EMS should behave such as the invocation of the `$evaluate` and creating encounter reports, as well as transformations from the HAPI Library's FHIR Model to our own domain model.
- Repository & Registry - This layer is for data access containing JPA repositories for the MySQL database as well as registry's for accessing static data.

There are also packages for:

- Utilities
- Configuration (For the database, spring, security and fhir server)
- Auditing and Logging

Static resources are provided in `resources/organisation` and `resources/practitioner`.

### UI
The front end is written with Angular 6 and is found in `EMS-UI/`

### Tests
Tests for the EMS are minimal. There are a few unit tests and spring boot tests for various Service Layer classes and a few of the utility classes in `src/test/java...`

A manual test pack for various CDS scenarios is located here and this can be used to ensure the EMS can handle the display of various parts of the $evaluate interaction.

## Licence

Unless stated otherwise, the codebase is released under [the MIT License][mit].
This covers both the codebase and any sample code in the documentation.

The documentation is [© Crown copyright][copyright] and available under the terms
of the [Open Government 3.0][ogl] licence.

[rvm]: https://www.ruby-lang.org/en/documentation/installation/#managers
[bundler]: http://bundler.io/
[mit]: LICENCE
[copyright]: http://www.nationalarchives.gov.uk/information-management/re-using-public-sector-information/uk-government-licensing-framework/crown-copyright/
[ogl]: http://www.nationalarchives.gov.uk/doc/open-government-licence/version/3/
