CREATE TABLE IF NOT EXISTS USERS(
    id INT NOT NULL,
    sub
    BIGINT
    NOT
    NULL,
    name
    VARCHAR
(
    255
),
    email VARCHAR
(
    255
),
    PRIMARY KEY
(
    id
)
    );

CREATE INDEX IF NOT EXISTS idx_users_sub ON USERS (sub);

CREATE TABLE IF NOT EXISTS GROUPS
(
    id
    INT
    NOT
    NULL,
    name VARCHAR(255),
    PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS GROUP_USER
(
    id
    INT
    NOT
    NULL,
    user_id
    INT
    NOT
    NULL,
    group_id
    INT
    NOT
    NULL,
    PRIMARY
    KEY
(
    id
),
    FOREIGN KEY
(
    user_id
) REFERENCES USERS
(
    id
),
    FOREIGN KEY
(
    group_id
) REFERENCES GROUPS
(
    id
)
    );
