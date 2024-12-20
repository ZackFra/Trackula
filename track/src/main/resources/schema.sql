CREATE TABLE IF NOT EXISTS users (
    username VARCHAR_IGNORECASE(50) NOT NULL PRIMARY KEY,
    password VARCHAR_IGNORECASE(500) NOT NULL,
    enabled BOOLEAN NOT NULL
);

CREATE TABLE IF NOT EXISTS authorities (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    username VARCHAR_IGNORECASE(50) NOT NULL,
    authority VARCHAR_IGNORECASE(50) NOT NULL,
    CONSTRAINT fk_authorities_users FOREIGN KEY(username) REFERENCES users(username)
);

CREATE UNIQUE INDEX IF NOT EXISTS ix_auth_username ON authorities (
    username,
    authority
);

CREATE TABLE IF NOT EXISTS category (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name TEXT,
    owner VARCHAR_IGNORECASE(50),
    FOREIGN KEY (owner) REFERENCES users(username)
);

CREATE TABLE IF NOT EXISTS timer_entry (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    owner VARCHAR_IGNORECASE(50),
    time_tracked BIGINT,
    FOREIGN KEY (owner) REFERENCES users(username)
);

CREATE TABLE IF NOT EXISTS timer_entry_category (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    category_id BIGINT NOT NULL,
    timer_entry_id BIGINT NOT NULL,
    owner VARCHAR_IGNORECASE(50),
    FOREIGN KEY (owner) REFERENCES users(username),
    FOREIGN KEY (category_id) REFERENCES category(id),
    FOREIGN KEY (timer_entry_id) REFERENCES timer_entry(id)
);