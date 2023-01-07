DROP TABLE  IF EXISTS USERS,ITEMS,BOOKINGS,COMMENTS;

CREATE TABLE IF NOT EXISTS USERS
(
    id    bigint GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY ,
    username   varchar(200) NOT NULL,
    email      varchar(30) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS ITEMS
(
    id      bigint GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY ,
    name         varchar(200) NOT NULL,
    description  varchar NOT NULL,
    is_available boolean NOT NULL,
    owner_id     bigint REFERENCES USERS(id) ON DELETE CASCADE,
    request_id   bigint
);

CREATE TABLE IF NOT EXISTS REQUESTS (
    id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    description varchar(2000),
    requester_id bigint REFERENCES USERS(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS BOOKINGS (
    id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    start_date TIMESTAMP WITHOUT TIME ZONE,
    end_date TIMESTAMP WITHOUT TIME ZONE,
    item_id bigint REFERENCES ITEMS(id) ON DELETE CASCADE,
    booker_id bigint REFERENCES USERS(id) ON DELETE CASCADE,
    status varchar(50)
);

CREATE TABLE IF NOT EXISTS COMMENTS (
    id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    text varchar(2000),
    item_id bigint REFERENCES ITEMS(id) ON DELETE CASCADE,
    author_id bigint REFERENCES USERS(id) ON DELETE CASCADE,
    created_date TIMESTAMP WITHOUT TIME ZONE
);