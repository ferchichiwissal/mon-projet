-- Schema for Supabase PostgreSQL database

-- Users table (abstract class, not directly created)
-- Each concrete user type will have its own table due to TABLE_PER_CLASS inheritance

-- Admin table
CREATE TABLE IF NOT EXISTS admin (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    email VARCHAR(255) UNIQUE,
    birth_date DATE,
    tel VARCHAR(255),
    address VARCHAR(255),
    photo_profil BYTEA,
    gender VARCHAR(255),
    created_at DATE,
    updated_at DATE,
    role VARCHAR(255),
    password VARCHAR(255),
    is_active BOOLEAN,
    age INTEGER
);

-- Doctor table
CREATE TABLE IF NOT EXISTS doctor (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    email VARCHAR(255) UNIQUE,
    birth_date DATE,
    tel VARCHAR(255),
    address VARCHAR(255),
    photo_profil BYTEA,
    gender VARCHAR(255),
    created_at DATE,
    updated_at DATE,
    role VARCHAR(255),
    password VARCHAR(255),
    is_active BOOLEAN,
    age INTEGER,
    cabinet_id BIGINT
);

-- CabinetDr table
CREATE TABLE IF NOT EXISTS cabinet_dr (
    id_site BIGSERIAL PRIMARY KEY,
    address VARCHAR(255),
    fax VARCHAR(255),
    tel VARCHAR(255),
    name VARCHAR(255),
    tax_number VARCHAR(255),
    created_at DATE,
    updated_at DATE,
    qr_code VARCHAR(255) UNIQUE NOT NULL -- Added QR code column
);

-- Assistant table
CREATE TABLE IF NOT EXISTS assistants (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    email VARCHAR(255) UNIQUE,
    birth_date DATE,
    tel VARCHAR(255),
    address VARCHAR(255),
    photo_profil BYTEA,
    gender VARCHAR(255),
    created_at DATE,
    updated_at DATE,
    role VARCHAR(255),
    password VARCHAR(255),
    is_active BOOLEAN,
    age INTEGER,
    cabinet_id BIGINT NOT NULL
);

-- Patient table
CREATE TABLE IF NOT EXISTS patients (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    email VARCHAR(255) UNIQUE,
    birth_date DATE,
    tel VARCHAR(255),
    address VARCHAR(255),
    photo_profil BYTEA,
    gender VARCHAR(255),
    created_at DATE,
    updated_at DATE,
    role VARCHAR(255),
    password VARCHAR(255),
    is_active BOOLEAN,
    age INTEGER,
    cabinet_id BIGINT NOT NULL -- Added foreign key to CabinetDr
);

-- Remove junction table cabinet_patient
-- CREATE TABLE IF NOT EXISTS cabinet_patient (
--     cabinet_id BIGINT,
--     patient_id BIGINT,
--     PRIMARY KEY (cabinet_id, patient_id),
--     FOREIGN KEY (cabinet_id) REFERENCES cabinet_dr(id_site),
--     FOREIGN KEY (patient_id) REFERENCES patients(id)
-- );

-- Password Reset Token table
CREATE TABLE IF NOT EXISTS password_reset_token (
    id BIGSERIAL PRIMARY KEY,
    token VARCHAR(255),
    user_id BIGINT,
    expiry_date TIMESTAMP
);

-- Add foreign key constraints
ALTER TABLE doctor 
ADD CONSTRAINT fk_doctor_cabinet 
FOREIGN KEY (cabinet_id) REFERENCES cabinet_dr(id_site);

ALTER TABLE assistants 
ADD CONSTRAINT fk_assistant_cabinet
FOREIGN KEY (cabinet_id) REFERENCES cabinet_dr(id_site);

-- Add foreign key constraint for Patient to CabinetDr
ALTER TABLE patients
ADD CONSTRAINT fk_patient_cabinet
FOREIGN KEY (cabinet_id) REFERENCES cabinet_dr(id_site);

-- Note: The password_reset_token table has a user_id column that should reference a user,
-- but since we're using TABLE_PER_CLASS inheritance, there's no single users table to reference.
-- In a real implementation, you might need to handle this differently, such as using a discriminator
-- column to identify which user table to join with.
