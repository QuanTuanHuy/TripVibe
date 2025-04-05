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

CREATE TABLE IF NOT EXISTS locations (
    id BIGSERIAL PRIMARY KEY,
    country_id BIGINT NOT NULL,
    province_id BIGINT NOT NULL,
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    detail TEXT,
    name TEXT NOT NULL,
    
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    
    -- Adding foreign key constraints (uncomment if these tables exist)
    CONSTRAINT fk_country FOREIGN KEY (country_id) REFERENCES countries(id),
    CONSTRAINT fk_province FOREIGN KEY (province_id) REFERENCES provinces(id)
);

-- Optional index creation for better performance on frequent queries
CREATE INDEX idx_locations_country ON locations(country_id);
CREATE INDEX idx_locations_province ON locations(province_id);


-- category for attractions
CREATE TABLE IF NOT EXISTS categories (
    id BIGSERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    description TEXT,
    icon_url TEXT,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    
    -- Audit fields (assuming these come from AuditModel)
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Optional: Create an index on name for faster lookups
CREATE INDEX idx_categories_name ON categories(name);


-- languages
CREATE TABLE IF NOT EXISTS languages (
    id BIGSERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    code VARCHAR(10) NOT NULL UNIQUE,
    
    -- Audit fields
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Index for language code lookups
CREATE INDEX idx_languages_code ON languages(code);

-- Create attractions table
CREATE TABLE IF NOT EXISTS attractions (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    location_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    
    -- Audit fields
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_attraction_location FOREIGN KEY (location_id) REFERENCES locations(id),
    CONSTRAINT fk_attraction_category FOREIGN KEY (category_id) REFERENCES categories(id)
);

-- Create attraction_schedules table
CREATE TABLE IF NOT EXISTS attraction_schedules (
    id BIGSERIAL PRIMARY KEY,
    attraction_id BIGINT NOT NULL,
    day_of_week INT NOT NULL, -- 0 = Sunday, 1 = Monday, etc.
    open_time TIME NOT NULL,
    close_time TIME NOT NULL,
    is_closed BOOLEAN NOT NULL DEFAULT FALSE,
    season_start DATE,
    season_end DATE,
    
    -- Audit fields
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_schedule_attraction FOREIGN KEY (attraction_id) REFERENCES attractions(id)
);

-- Create images table
CREATE TABLE IF NOT EXISTS images (
    id BIGSERIAL PRIMARY KEY,
    entity_id BIGINT NOT NULL,
    url VARCHAR(1000) NOT NULL,
    entity_type VARCHAR(50) NOT NULL,
    is_primary BOOLEAN NOT NULL DEFAULT FALSE,
    
    -- Audit fields
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for better performance
CREATE INDEX idx_attraction_location ON attractions(location_id);
CREATE INDEX idx_attraction_category ON attractions(category_id);
CREATE INDEX idx_schedule_attraction ON attraction_schedules(attraction_id);
CREATE INDEX idx_image_entity ON images(entity_id, entity_type);

-- Create attraction_languages table
CREATE TABLE IF NOT EXISTS attraction_languages (
    id BIGSERIAL PRIMARY KEY,
    attraction_id BIGINT NOT NULL,
    language_id BIGINT NOT NULL,
    
    -- Audit fields
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    
    -- Constraints
    CONSTRAINT fk_attraction_language_attraction FOREIGN KEY (attraction_id) REFERENCES attractions(id),
    CONSTRAINT fk_attraction_language_language FOREIGN KEY (language_id) REFERENCES languages(id),
    CONSTRAINT attraction_language_unique UNIQUE (attraction_id, language_id)
);

-- Create indexes
CREATE INDEX idx_attraction_language_attraction ON attraction_languages(attraction_id);
CREATE INDEX idx_attraction_language_language ON attraction_languages(language_id);
