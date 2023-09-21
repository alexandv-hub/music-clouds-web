CREATE TABLE notification (
      notification_id serial PRIMARY KEY,
      to_user_id INTEGER,
      to_user_email VARCHAR(255),
      sender VARCHAR(255),
      message VARCHAR(255),
      sent_at TIMESTAMP(6)
);

ALTER TABLE notification
    OWNER TO username;
