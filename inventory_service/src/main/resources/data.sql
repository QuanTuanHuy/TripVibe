-- Sample data for development environment
-- This script will be executed automatically by Spring Boot if spring.sql.init.mode=always

-- Create tables if not exist
CREATE TABLE IF NOT EXISTS properties (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    address VARCHAR(255),
    city VARCHAR(100),
    state VARCHAR(100),
    country VARCHAR(100),
    zip_code VARCHAR(20),
    latitude VARCHAR(50),
    longitude VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS room_types (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    max_occupancy INTEGER,
    max_adults INTEGER,
    max_children INTEGER,
    base_price DECIMAL(10, 2)
);

CREATE TABLE IF NOT EXISTS rooms (
    id SERIAL PRIMARY KEY,
    room_number VARCHAR(20) NOT NULL,
    name VARCHAR(100),
    description TEXT,
    room_type_id INTEGER REFERENCES room_types(id),
    property_id INTEGER REFERENCES properties(id),
    status VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS room_availabilities (
    id SERIAL PRIMARY KEY,
    room_id INTEGER REFERENCES rooms(id),
    date DATE NOT NULL,
    status VARCHAR(50),
    price DECIMAL(10, 2),
    base_price DECIMAL(10, 2),
    booking_id VARCHAR(100),
    lock_id VARCHAR(100),
    lock_expiration_date DATE,
    last_modified TIMESTAMP,
    needs_cleaning BOOLEAN DEFAULT FALSE,
    needs_maintenance BOOLEAN DEFAULT FALSE,
    maintenance_notes TEXT,    guest_count INTEGER,
    version INTEGER DEFAULT 0,
    CONSTRAINT uq_room_date UNIQUE(room_id, date)
);

-- Create indexes for performance
CREATE INDEX IF NOT EXISTS idx_room_date ON room_availabilities(room_id, date);
CREATE INDEX IF NOT EXISTS idx_booking_id ON room_availabilities(booking_id);
CREATE INDEX IF NOT EXISTS idx_status_lock_expiration ON room_availabilities(status, lock_expiration_date);
CREATE INDEX IF NOT EXISTS idx_date_status ON room_availabilities(date, status);

-- Insert sample data
-- Property
INSERT INTO properties (name, description, address, city, state, country, zip_code, latitude, longitude)
VALUES ('Sample Hotel', 'A beautiful hotel in the heart of the city', '123 Main Street', 'New York', 'NY', 'USA', '10001', '40.7128', '-74.0060')
ON CONFLICT DO NOTHING;

-- Room Types
INSERT INTO room_types (name, description, max_occupancy, max_adults, max_children, base_price)
VALUES 
    ('Standard Room', 'Comfortable room with all basic amenities', 2, 2, 0, 100.00),
    ('Deluxe Room', 'Spacious room with additional amenities', 3, 2, 1, 150.00),
    ('Suite', 'Luxury suite with separate living area', 4, 2, 2, 250.00)
ON CONFLICT DO NOTHING;

-- Rooms
INSERT INTO rooms (room_number, name, description, room_type_id, property_id, status)
VALUES 
    ('101', 'Standard Room 101', 'Standard room on the first floor', 1, 1, 'AVAILABLE'),
    ('102', 'Standard Room 102', 'Standard room on the first floor', 1, 1, 'AVAILABLE'),
    ('201', 'Deluxe Room 201', 'Deluxe room on the second floor', 2, 1, 'AVAILABLE'),
    ('202', 'Deluxe Room 202', 'Deluxe room on the second floor', 2, 1, 'AVAILABLE'),
    ('301', 'Suite 301', 'Luxury suite on the third floor', 3, 1, 'AVAILABLE')
ON CONFLICT DO NOTHING;
