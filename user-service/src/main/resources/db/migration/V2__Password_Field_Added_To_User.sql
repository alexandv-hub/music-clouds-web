ALTER TABLE _user
    ADD COLUMN password VARCHAR(255) NOT NULL DEFAULT 'default_password';
