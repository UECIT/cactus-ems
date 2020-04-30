INSERT INTO
 cdss_decoupling.patient (id, title, first_name, last_name, date_of_birth, gender, nhs_number, address, city, postal_code, home_phone, mobile, email, language)
VALUES
 (1, "Miss", "Joanne", "Bloggs", "2011-09-07", "female", "9476719915", "Flat 3, 123 A Road", "London", "W1 123", "020 123 456", "0700 123 456", null, "en"),
 (2, "Mrs", "Jenny", "Bloggs", "1942-09-07", "female", "9476719916", "Flat 3, 123 A Road", "London", "W1 123", "020 123 456", "0700 123 456", null, "en"),
 (3, "Mr", "Joe", "Bloggs", "1979-09-07", "male", "9476719917", "Flat 3, 123 A Road", "London", "W1 123", "020 123 456", "0700 123 456", "joe.bloggs@example.com", "en"),
 (4, "Miss", "Hatty", "Bloggs", "1949-09-07", "female", "9476719918", "Flat 3, 123 A Road", "London", "W1 123", "020 123 456", "0700 123 456", "hatty.bloggs@example.com", "en"),
 (5, "Miss", "John", "Bloggs", "1967-09-07", "male", "9476719919", "Flat 3, 123 A Road", "London", "W1 123", "020 123 456", "0700 123 456", "john.bloggs@example.com", "en"),
 (6, "Miss", "Karen", "Bloggs", "1985-09-07", "female", "9476719920", "Flat 3, 123 A Road", "London", "W1 123", "020 123 456", "0700 123 456", "karen.bloggs@example.com", "en");

/*St. Georges Test Patients*/
INSERT INTO
 cdss_decoupling.patient (id, first_name, last_name, date_of_birth, gender, nhs_number, address, city, postal_code, home_phone, mobile, language)
VALUES
 (7, "Testing-01", "STGTesting", "1970-01-01", "male", "5900086461", "St Georges", "Leeds", "LS1 1AA", "111 111 111", "0799 999 999", "en"),
 (8, "CAB - TEST", "STGTESTING", "1988-01-08", "male", "5900029409", "Flat 1 77A", "Leeds", "LS1 1AA", "111 111 111", "0799 999 999", "en"),
 (9, "ers1", "STGTesting", "1988-01-08", "male", "5900081249", "St Georges", "Leeds", "LS1 1AA", "111 111 111", "0799 999 999", "en"),
 (10, "ers2", "STGtesting", "1988-01-08", "female", "5900089398", "St Georges", "Leeds", "LS1 1AA", "111 111 111", "0799 999 999", "en"),
 (11, "ers3", "STGTesting", "1988-01-08", "unknown", "5900082393", "St Georges", "Leeds", "LS1 1AA", "111 111 111", "0799 999 999", "en"),
 (12, "ers4", "STGTESTING", "1988-01-08", "female", "5900080641", "St Georges", "Leeds", "LS1 1AA", "111 111 111", "0799 999 999", "en"),
 (13, "ers5", "STGTesting", "1988-01-08", "unknown", "5900097323", "St Georges", "Leeds", "LS1 1AA", "111 111 111", "0799 999 999", "en"),
 (14, "Chris1", "STGTESTING", "1988-01-08", "male", "5900086305", "St Georges", "Leeds", "LS1 1AA", "111 111 111", "0799 999 999", "en"),
 (15, "Chris2", "STGTESTING", "1988-01-08", "female", "5900097943", "St Georges", "Leeds", "LS1 1AA", "111 111 111", "0799 999 999", "en"),
 (16, "Chris3", "STGTESTING", "1988-01-08", "female", "5900094766", "St Georges", "Leeds", "LS1 1AA", "111 111 111", "0799 999 999", "en");


INSERT INTO
 cdss_decoupling.users (username, name, password, enabled, role)
VALUES
 ('admin', 'Admin User', '$2a$10$hbxecwitQQ.dDT4JOFzQAulNySFwEpaFLw38jda6Td.Y/cOiRzDFu', true, 'ROLE_ADMIN');

INSERT INTO
 cdss_decoupling.cdss_supplier (id, name, base_url)
VALUES
 (1, 'CDSS Stub', 'http://ec2-3-8-182-163.eu-west-2.compute.amazonaws.com:8080/cdss-supplier-stub/fhir/'),
 (2, 'CDSS Local', 'http://localhost:8080/fhir/');

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
 (16, 1, 8, 'None of the Above Scenario'),
 (17, 1, 9, 'Mental Health Table Scenario'),
 (20, 2, 5, 'Palpitations simple'),
 (21, 2, 6, 'Palpitations extended'),
 (22, 2, 7, 'Anxiety');

INSERT INTO
 cdss_decoupling.user_cdss_supplier (username, cdss_supplier_id)
VALUES
 ('admin', 1);
