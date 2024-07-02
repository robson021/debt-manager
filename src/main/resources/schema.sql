CREATE TABLE IF NOT EXISTS USERS(
  id SERIAL PRIMARY KEY,
  sub VARCHAR (255) UNIQUE NOT NULL,
  name VARCHAR (255) NOT NULL,
  email VARCHAR (255) UNIQUE NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_users_sub ON USERS (sub);

CREATE TABLE IF NOT EXISTS GROUPS (
  id SERIAL PRIMARY KEY,
  owner_id INT NOT NULL,
  name VARCHAR(255) NOT NULL,
  FOREIGN KEY (owner_id) REFERENCES USERS (id)
);

ALTER TABLE GROUPS ADD CONSTRAINT IF NOT EXISTS UQ_GROUPS UNIQUE(owner_id, name);

CREATE TABLE IF NOT EXISTS GROUP_USER (
  id SERIAL PRIMARY KEY,
  user_id INT NOT NULL,
  group_id INT NOT NULL,
  FOREIGN KEY (user_id) REFERENCES USERS (id),
  FOREIGN KEY (group_id) REFERENCES GROUPS (id)
);

ALTER TABLE GROUP_USER ADD CONSTRAINT IF NOT EXISTS UQ_GROUP_USER UNIQUE(user_id, group_id);

CREATE TABLE IF NOT EXISTS DEBTS (
  id SERIAL PRIMARY KEY,
  amount DECIMAL(19,2),
  lender_id INT NOT NULL,
  borrower_id INT NOT NULL,
  group_id INT NOT NULL,
  FOREIGN KEY (lender_id) REFERENCES USERS (id),
  FOREIGN KEY (borrower_id) REFERENCES USERS (id),
  FOREIGN KEY (group_id) REFERENCES GROUPS (id)
);
