-- Insert regions
INSERT INTO regions (name) VALUES ('Nairobi');
INSERT INTO regions (name) VALUES ('Mombasa');
INSERT INTO regions (name) VALUES ('Kisumu');

-- Insert cars
INSERT INTO cars (plate_number, model, color, created_at) VALUES ('KDA 123A', 'Toyota Hilux', 'White', NOW());
INSERT INTO cars (plate_number, model, color, created_at) VALUES ('KDB 456B', 'Isuzu D-Max', 'Silver', NOW());
INSERT INTO cars (plate_number, model, color, created_at) VALUES ('KDC 789C', 'Ford Ranger', 'Black', NOW());

-- Insert users
-- Admin
INSERT INTO users (first_name, middle_name, last_name, password, phone_number, roles, email, address)
VALUES ('Admin', 'User', 'One', 'password', '0712345678', 'ADMIN', 'admin@example.com', '123 Admin St');

-- Reporters
INSERT INTO users (first_name, middle_name, last_name, password, phone_number, roles, email, address)
VALUES ('Reporter', 'User', 'One', 'password', '0711111111', 'REPORTER', 'reporter1@example.com', '111 Reporter St');
INSERT INTO users (first_name, middle_name, last_name, password, phone_number, roles, email, address)
VALUES ('Reporter', 'User', 'Two', 'password', '0722222222', 'REPORTER', 'reporter2@example.com', '222 Reporter St');

-- Drivers
INSERT INTO users (first_name, middle_name, last_name, password, phone_number, roles, email, address, license_number, status, car_id, region_id)
VALUES ('Driver', 'User', 'One', 'password', '0733333333', 'DRIVER', 'driver1@example.com', '333 Driver St', 'DL12345', 'ACTIVE', 1, 1);
INSERT INTO users (first_name, middle_name, last_name, password, phone_number, roles, email, address, license_number, status, car_id, region_id)
VALUES ('Driver', 'User', 'Two', 'password', '0744444444', 'DRIVER', 'driver2@example.com', '444 Driver St', 'DL67890', 'ACTIVE', 2, 2);
INSERT INTO users (first_name, middle_name, last_name, password, phone_number, roles, email, address, license_number, status, car_id, region_id)
VALUES ('Driver', 'User', 'Three', 'password', '0755555555', 'DRIVER', 'driver3@example.com', '555 Driver St', 'DL54321', 'ACTIVE', 3, 3);
