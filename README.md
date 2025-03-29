# Database Migration to Supabase

This project has been configured to use Supabase as the database provider. Below are instructions on how to migrate from MySQL to Supabase.

## Configuration Changes

The following changes have been made to migrate the database to Supabase:

1. Added PostgreSQL dependency to `pom.xml`
2. Updated `application.properties` to connect to Supabase
3. Created a `schema.sql` file for reference

## Supabase Connection Details

- **Project URL**: https://qfdzsjqrlhdjcozqpxga.supabase.co
- **API Key**: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InFmZHpzanFybGhkamNvenFweGdhIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDMyMDI5ODksImV4cCI6MjA1ODc3ODk4OX0.TgkB-CPqeHkvYW6XqMcAPRNhSlGIkE_BfefnNqpX5Bo

## How to Run the Application

1. Make sure you have Java 17 installed
2. Build the project with Maven:
   ```
   mvn clean install
   ```
3. Run the application:
   ```
   mvn spring-boot:run
   ```

The application will automatically create the necessary tables in Supabase when it starts, thanks to the `spring.jpa.hibernate.ddl-auto=update` setting in `application.properties`.

## Manual Database Setup (if needed)

If you need to manually create the database schema, you can use the `schema.sql` file located in `src/main/resources/`. This file contains the SQL statements to create all the necessary tables.

You can execute this script in the Supabase SQL Editor:

1. Log in to your Supabase dashboard
2. Go to the SQL Editor
3. Copy the contents of `schema.sql`
4. Paste into the SQL Editor and run

## Entity Relationship Diagram

The application uses the following entity structure:

- `User` (abstract class)
  - `Admin` (extends User)
  - `Doctor` (extends User)
  - `Assistant` (extends User)
  - `Patient` (extends User)
- `CabinetDr`
- `PasswordResetToken`

The inheritance strategy is TABLE_PER_CLASS, which means each concrete user type has its own table.

## Notes on Data Migration

If you need to migrate existing data from MySQL to Supabase, you can:

1. Export data from MySQL as CSV files
2. Import the CSV files into Supabase using the Supabase dashboard

Alternatively, you can write a migration script that reads from MySQL and writes to Supabase.

## Troubleshooting

- If you encounter connection issues, verify that the Supabase connection details in `application.properties` are correct
- If tables are not being created automatically, you can manually create them using the `schema.sql` file
- For BYTEA (binary) data like profile photos, ensure proper encoding/decoding in your application code
