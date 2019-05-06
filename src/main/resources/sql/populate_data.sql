INSERT INTO
 cdss_decoupling.patient (id, title, first_name, last_name, address, date_of_birth, gender, nhs_number) 
VALUES
 (1, "Miss", "Joanne", "Bloggs", null, "2011-09-07", "female", "9476719915"),
 (2, "Mrs", "Jenny", "Bloggs", null, "1942-09-07", "female", "9476719916"),
 (3, "Mr", "Joe", "Bloggs", null, "1979-09-07", "male", "9476719915"),
 (4, "Miss", "Hatty", "Bloggs", null, "1949-09-07", "female", "9476719915"),
 (5, "Miss", "John", "Bloggs", null, "1967-09-07", "male", "9476719915"),
 (6, "Miss", "Karen", "Bloggs", null, "1985-09-07", "female", "9476719915");

INSERT INTO
 cdss_decoupling.skillset (id, code, description) 
VALUES
 (1, 'CH', 'Call Handler'),
 (2, 'CL', 'Clinician'),
 (3, 'MH', 'Mental Health Specialist'),
 (4, 'PA', 'Patient');

INSERT INTO
 cdss_decoupling.party (id, code, description) 
VALUES
 (1, '1', '1st Party'),
 (2, '3', '3rd Party');

INSERT INTO
 cdss_decoupling.test_scenario (id, patient_id, party_id, skillset_id, test_case_summary) 
VALUES
 (1, 1, 2, 1, 'Scenario 1'),
 (2, 2, 1, 1, 'Scenario 2'),
 (3, 3, 1, 4, 'Scenario 3'),
 (4, 4, 1, 1, 'Scenario 4'),
 (5, 5, 1, 1, 'Scenario 5'),
 (6, 6, 1, 1, 'Scenario 6');
 
INSERT INTO
 cdss_decoupling.users (username, name, password, enabled, role) 
VALUES
 ('admin', 'Admin User', '$2a$10$hbxecwitQQ.dDT4JOFzQAulNySFwEpaFLw38jda6Td.Y/cOiRzDFu', true, 'ROLE_ADMIN'),
 ('nhs-user', 'NHS User', '$2a$10$hbxecwitQQ.dDT4JOFzQAulNySFwEpaFLw38jda6Td.Y/cOiRzDFu', true, 'ROLE_NHS'),
 ('cdss-supplier', 'CDSS Supplier', '$2a$10$hbxecwitQQ.dDT4JOFzQAulNySFwEpaFLw38jda6Td.Y/cOiRzDFu', true, 'ROLE_CDSS');

INSERT INTO
 cdss_decoupling.cdss_supplier (id, name, base_url) 
VALUES
 (1, 'CDSS Stub', 'http://localhost:8080/fhir/');

INSERT INTO
 cdss_decoupling.service_definition (id, cdss_supplier_id, service_definition_id, description) 
VALUES
 (1, 1, 1, 'Vomiting and fever'),
 (2, 1, 2, 'Headache'),
 (3, 1, 3, 'Leg Injury, Blunt Trauma'),
 (4, 1, 4, 'Cut to right hand 7 days ago'),
 (5, 1, 5, 'Palpitations past 2 hours and currently experiencing'),
 (6, 1, 6, 'Fallen and hurt right knee'),
 (7, 1, 7, 'Mental Health Scenario'),
 (8, 1, 8, 'None of the Above Scenario'),
 (9, 1, 9, 'Mental Health Table Scenario');

INSERT INTO
 cdss_decoupling.user_cdss_supplier (username, cdss_supplier_id)
VALUES
 ('cdss-supplier', 1);
