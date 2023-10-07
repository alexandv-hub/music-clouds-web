-- Define the ENUM type for PostgreSQL
CREATE TYPE token_type AS ENUM ('BEARER');

CREATE TABLE token (
           id SERIAL PRIMARY KEY,
           token VARCHAR(255) UNIQUE NOT NULL,
--            token_type token_type DEFAULT 'BEARER',
           token_type VARCHAR(10) DEFAULT 'BEARER' CHECK (token_type IN ('BEARER')),
           revoked BOOLEAN DEFAULT FALSE,
           expired BOOLEAN DEFAULT FALSE,
           user_id INT,
           FOREIGN KEY (user_id) REFERENCES _user(id)
);
