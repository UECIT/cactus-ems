USE cdss_decoupling;

/* Destroy all existing data */
DROP TABLE IF EXISTS cdss_decoupling.test_scenario;
DROP TABLE IF EXISTS cdss_decoupling.patient;
DROP TABLE IF EXISTS cdss_decoupling.case_carePlan;
DROP TABLE IF EXISTS cdss_decoupling.case_immunization;
DROP TABLE IF EXISTS cdss_decoupling.case_observation;
DROP TABLE IF EXISTS cdss_decoupling.case_medication;
DROP TABLE IF EXISTS cdss_decoupling.cases;
DROP TABLE IF EXISTS cdss_decoupling.skillset;
DROP TABLE IF EXISTS cdss_decoupling.party;
DROP TABLE IF EXISTS cdss_decoupling.user_cdss_supplier;
DROP TABLE IF EXISTS cdss_decoupling.users;
DROP TABLE IF EXISTS cdss_decoupling.user_roles;
DROP TABLE IF EXISTS cdss_decoupling.service_definition;
DROP TABLE IF EXISTS cdss_decoupling.cdss_supplier;
DROP TABLE IF EXISTS cdss_decoupling.referral_request;
DROP TABLE IF EXISTS cdss_decoupling.audit_entry;
DROP TABLE IF EXISTS cdss_decoupling.audit_record;

/* Create new table schemas */

CREATE TABLE cdss_decoupling.audit_record
(
    id              BIGINT     NOT NULL AUTO_INCREMENT,
    case_id         BIGINT(20) NULL,
    closed_date     DATETIME   NULL,
    created_date    DATETIME   NULL,
    triage_complete BOOLEAN    NULL,
    PRIMARY KEY (id)
);

CREATE TABLE cdss_decoupling.audit_entry
(
    id                               BIGINT     NOT NULL AUTO_INCREMENT,
    audit_record_id                  BIGINT     NULL,
    cdss_questionnaire_request       LONGTEXT   NULL,
    cdss_questionnaire_response      LONGTEXT   NULL,
    cdss_service_definition_request  LONGTEXT   NULL,
    cdss_service_definition_response LONGTEXT   NULL,
    created_date                     DATETIME   NULL,
    test_harness_request             LONGTEXT   NULL,
    test_harness_response            LONGTEXT   NULL,
    contained                        LONGTEXT   NULL,
    type                             BIGINT(20) NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (audit_record_id) REFERENCES cdss_decoupling.audit_record (id)
);

CREATE TABLE cdss_decoupling.patient
(
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    title         VARCHAR(20)  NULL,
    first_name    VARCHAR(100) NULL,
    last_name     VARCHAR(100) NULL,
    address       VARCHAR(255) NULL,
    city          VARCHAR(255) NULL,
    postal_code   VARCHAR(255) NULL,
    home_phone    VARCHAR(255) NULL,
    mobile        VARCHAR(255) NULL,
    email         VARCHAR(255) NULL,
    date_of_birth DATE         NULL,
    gender        VARCHAR(20)  NULL,
    nhs_number    VARCHAR(10)  NULL,
    language      VARCHAR(10)  NULL,
    PRIMARY KEY (id)
);

CREATE TABLE cdss_decoupling.skillset
(
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    code        VARCHAR(250) NOT NULL,
    description VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE cdss_decoupling.party
(
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    code        VARCHAR(250) NOT NULL,
    description VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE cdss_decoupling.test_scenario
(
    id                BIGINT       NOT NULL AUTO_INCREMENT,
    patient_id        BIGINT       NULL,
    party_id          BIGINT       NULL,
    skillset_id       BIGINT       NULL,
    test_case_summary VARCHAR(255) NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (patient_id) REFERENCES cdss_decoupling.patient (id),
    FOREIGN KEY (party_id) REFERENCES cdss_decoupling.party (id),
    FOREIGN KEY (skillset_id) REFERENCES cdss_decoupling.skillset (id)
);

create table cdss_decoupling.referral_request
(
    id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    case_id  BIGINT   NULL,
    resource LONGTEXT NULL,
    FOREIGN KEY (case_id) REFERENCES cases (id)
);

CREATE TABLE cdss_decoupling.cases
(
    id             BIGINT       NOT NULL AUTO_INCREMENT,
    firstName      VARCHAR(100) NULL,
    lastName       VARCHAR(100) NULL,
    gender         VARCHAR(20)  NULL,
    date_of_birth  DATE         NULL,
    address        VARCHAR(255) NULL,
    city           VARCHAR(255) NULL,
    postal_code    VARCHAR(255) NULL,
    home_phone     VARCHAR(255) NULL,
    mobile         VARCHAR(255) NULL,
    email          VARCHAR(255) NULL,
    nhs_number     VARCHAR(10)  NULL,
    skillset_id    BIGINT       NULL,
    party_id       BIGINT       NULL,
    session_id     VARCHAR(2500),
    case_timestamp DATETIME     NULL,
    patientId      VARCHAR(255) NULL,
    practitionerId VARCHAR(255) NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (party_id) REFERENCES cdss_decoupling.party (id),
    FOREIGN KEY (skillset_id) REFERENCES cdss_decoupling.skillset (id)
);

CREATE TABLE cdss_decoupling.case_carePlan
(
    `id`        bigint(20) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `reference` varchar(255) DEFAULT NULL,
    `timestamp` date         DEFAULT NULL,
    `case_id`   bigint(20)   DEFAULT NULL,
    FOREIGN KEY (`case_id`) REFERENCES `cases` (`id`)
);

CREATE TABLE cdss_decoupling.case_immunization
(
    id                     BIGINT       NOT NULL AUTO_INCREMENT,
    case_id                BIGINT       NULL,
    code                   VARCHAR(50)  NULL,
    display                VARCHAR(100) NULL,
    not_given              BOOLEAN      NULL,
    immunization_timestamp DATE         NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (case_id) REFERENCES cdss_decoupling.cases (id)
);

CREATE TABLE cdss_decoupling.case_medication
(
    id                   BIGINT       NOT NULL AUTO_INCREMENT,
    case_id              BIGINT       NULL,
    code                 VARCHAR(50)  NULL,
    display              VARCHAR(100) NULL,
    not_given            BOOLEAN      NULL,
    medication_timestamp DATE         NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (case_id) REFERENCES cdss_decoupling.cases (id)
);

CREATE TABLE cdss_decoupling.case_observation
(
    id                    BIGINT       NOT NULL AUTO_INCREMENT,
    case_id               BIGINT       NULL,
    code                  VARCHAR(50)  NULL,
    display               VARCHAR(100) NULL,
    data_absent_code      VARCHAR(50)  NULL,
    data_absent_display   VARCHAR(100) NULL,
    value                 BOOLEAN      NULL,
    observation_timestamp DATE         NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (case_id) REFERENCES cdss_decoupling.cases (id)
);

CREATE TABLE cdss_decoupling.case_parameter
(
    id                  BIGINT       NOT NULL AUTO_INCREMENT,
    case_id             BIGINT       NULL,
    name                VARCHAR(50)  NULL,
    value               VARCHAR(100) NULL,
    parameter_timestamp DATE         NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (case_id) REFERENCES cdss_decoupling.cases (id)
);

CREATE TABLE cdss_decoupling.users
(
    username VARCHAR(45)  NOT NULL,
    name     VARCHAR(100) NULL,
    password VARCHAR(200) NOT NULL,
    enabled  BOOLEAN      NOT NULL DEFAULT TRUE,
    role     VARCHAR(20)  NOT NULL,
    PRIMARY KEY (username)
);

CREATE TABLE cdss_decoupling.cdss_supplier
(
    id               BIGINT           NOT NULL AUTO_INCREMENT,
    name             VARCHAR(250)     NOT NULL,
    base_url         VARCHAR(255)     NOT NULL,
    referencing_type TINYINT UNSIGNED NOT NULL DEFAULT 1,
    PRIMARY KEY (id)
);

CREATE TABLE cdss_decoupling.user_cdss_supplier
(
    username         VARCHAR(45) NOT NULL,
    cdss_supplier_id BIGINT      NOT NULL,
    FOREIGN KEY (username) REFERENCES cdss_decoupling.users (username),
    FOREIGN KEY (cdss_supplier_id) REFERENCES cdss_decoupling.cdss_supplier (id)
);

CREATE TABLE cdss_decoupling.service_definition
(
    id                    BIGINT       NOT NULL AUTO_INCREMENT,
    cdss_supplier_id      BIGINT       NOT NULL,
    service_definition_id VARCHAR(250) NOT NULL,
    description           VARCHAR(255) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (cdss_supplier_id) REFERENCES cdss_decoupling.cdss_supplier (id)
);
