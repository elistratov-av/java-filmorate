CREATE TABLE IF NOT EXISTS genres (
  genre_id integer PRIMARY KEY,
  name varchar(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS mpa (
  mpa_id integer PRIMARY KEY,
  name varchar(50) NOT NULL,
  description varchar(100)
);

CREATE TABLE IF NOT EXISTS films (
  film_id integer GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  name varchar(255) NOT NULL,
  description varchar(200),
  release_date date,
  duration float,
  mpa_id integer,
  CONSTRAINT fk_mpa_films FOREIGN KEY (mpa_id) REFERENCES mpa (mpa_id)
);

CREATE TABLE IF NOT EXISTS users (
  user_id integer GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  email varchar(255) NOT NULL,
  login varchar(255) NOT NULL,
  user_name varchar(255) NOT NULL,
  birthday date
);

CREATE TABLE IF NOT EXISTS film_genres (
  film_id integer,
  genre_id integer,
  PRIMARY KEY (film_id, genre_id),
  CONSTRAINT fk_film_film_genres FOREIGN KEY (film_id) REFERENCES films (film_id),
  CONSTRAINT fk_genre_film_genres FOREIGN KEY (genre_id) REFERENCES genres (genre_id)
);

CREATE TABLE IF NOT EXISTS friends (
  user_id integer,
  friend_id integer,
  PRIMARY KEY (user_id, friend_id),
  CONSTRAINT fk_user_friends FOREIGN KEY (user_id) REFERENCES users (user_id),
  CONSTRAINT fk_friend_friends FOREIGN KEY (friend_id) REFERENCES users (user_id)
);

CREATE TABLE IF NOT EXISTS likes (
  film_id integer,
  user_id integer,
  PRIMARY KEY (film_id, user_id),
  CONSTRAINT fk_film_likes FOREIGN KEY (film_id) REFERENCES films (film_id),
  CONSTRAINT fk_user_likes FOREIGN KEY (user_id) REFERENCES users (user_id)
);
