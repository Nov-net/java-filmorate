CREATE ALIAS IF NOT EXISTS getDate AS
    'java.util.Date getDate() {
        return new java.util.Date();
    }';

CREATE TABLE IF NOT EXISTS users (
    id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    login text not null,
    name text,
    email text,
    birthday date,
    CONSTRAINT login_check
    CHECK (login NOT LIKE '% %' and login NOT LIKE ''),
    CONSTRAINT email_check
    CHECK (email LIKE '%@%'),
    CONSTRAINT birthday_check
    CHECK (CAST(birthday AS date) <= CAST(getDate() AS date))
    );

CREATE TABLE IF NOT EXISTS films (
    id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name text NOT NULL,
    description varchar(200),
    release_date date NOT NULL,
    duration int NOT NULL,
    CONSTRAINT name_check
    CHECK (name NOT LIKE '% %' and name NOT LIKE ''),
    CONSTRAINT duration_check
    CHECK (duration > 0),
    CONSTRAINT release_date_check
    CHECK (CAST(release_date AS date) > CAST('1895-12-27' AS date))
);

CREATE TABLE IF NOT EXISTS mpa (
    id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name text NOT NULL,
    CONSTRAINT name_check
    CHECK (name NOT LIKE '% %' and name NOT LIKE '')
);

CREATE TABLE IF NOT EXISTS genre (
    id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name text NOT NULL,
    CONSTRAINT name_check
    CHECK (name NOT LIKE '% %' and name NOT LIKE '')
);

CREATE TABLE IF NOT EXISTS film_mpa (
    film_id INTEGER NOT NULL REFERENCES films (id),
    mpa_id INTEGER NOT NULL REFERENCES mpa (id),
    PRIMARY KEY (film_id, mpa_id)
    );

CREATE TABLE IF NOT EXISTS film_genre (
    film_id INTEGER NOT NULL REFERENCES films (id),
    genre_id INTEGER NOT NULL REFERENCES genre (id),
    PRIMARY KEY (film_id, genre_id)
    );

CREATE TABLE IF NOT EXISTS likes (
    film_id INTEGER NOT NULL REFERENCES films (id),
    user_id INTEGER NOT NULL REFERENCES users (id),
    PRIMARY KEY (film_id, user_id)
    );

CREATE TABLE IF NOT EXISTS friends (
    user_id INTEGER NOT NULL REFERENCES users (id),
    friend_id INTEGER NOT NULL REFERENCES users (id),
    PRIMARY KEY (user_id, friend_id)
    );
