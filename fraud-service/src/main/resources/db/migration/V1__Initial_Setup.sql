CREATE TABLE fraud_check_history (
        id serial PRIMARY KEY,
        user_id INTEGER,
        is_fraudster BOOLEAN,
        created_at TIMESTAMP(6)
);

ALTER TABLE fraud_check_history
    OWNER TO username;
