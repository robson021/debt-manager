CREATE TABLE IF NOT EXISTS USERS(
  id SERIAL PRIMARY KEY,
  sub VARCHAR (255) UNIQUE NOT NULL,
  name VARCHAR (255) NOT NULL,
  email VARCHAR (255) UNIQUE NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_users_sub ON USERS (sub);

CREATE TABLE IF NOT EXISTS GROUPS (
  id SERIAL PRIMARY KEY,
  name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS GROUP_USER (
  id SERIAL PRIMARY KEY,
  user_id INT NOT NULL,
  group_id INT NOT NULL,
  FOREIGN KEY (user_id) REFERENCES USERS (id),
  FOREIGN KEY (group_id) REFERENCES GROUPS (id)
);
