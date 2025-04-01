create database db_location_service;

CREATE TABLE IF NOT EXISTS countries (
    id BIGSERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    code VARCHAR(10) NOT NULL,
    currency VARCHAR(10),
    timezone TEXT,
    language TEXT,
    region TEXT,
    sub_region TEXT,
    flag_url TEXT
);

-- Add unique constraint on country code
ALTER TABLE countries ADD CONSTRAINT unique_country_code UNIQUE (code);

-- Add indexes for commonly queried columns
CREATE INDEX idx_countries_name ON countries(name);


CREATE TABLE IF NOT EXISTS provinces (
    id BIGSERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    code TEXT NOT NULL,
    country_id BIGINT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    
    -- Foreign key constraint
    CONSTRAINT fk_country
        FOREIGN KEY (country_id)
        REFERENCES countries(id)
        ON DELETE CASCADE
);

-- Add unique constraint on province code
ALTER TABLE provinces ADD CONSTRAINT unique_province_code UNIQUE (code);

-- Add index for foreign key for better join performance
CREATE INDEX idx_provinces_country_id ON provinces(country_id);

-- Add index for commonly queried columns
CREATE INDEX idx_provinces_name ON provinces(name);
CREATE INDEX idx_provinces_code ON provinces(code);