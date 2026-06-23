-- Alter users table to make password column nullable for OAuth users
ALTER TABLE users MODIFY COLUMN password VARCHAR(255) NULL;
