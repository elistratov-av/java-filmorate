INSERT INTO films (name, description, release_date, duration, mpa_id)
VALUES
    ('Фильм 1', 'Интересный фильм', '2019-05-14', 15, 1),
    ('Фильм 2', 'Занимательный фильм', '2020-02-07', 2, 2),
    ('Фильм 3', 'Приключенческий фильм', '2021-08-25', 25, 3);

MERGE INTO film_genres (film_id, genre_id)
VALUES
    (1, 1),
    (2, 2),
    (2, 3),
    (3, 6);

INSERT INTO users (email, login, user_name, birthday)
VALUES
    ('mail1@yandex.ru', 'user1', 'Иванов', '1972-08-03'),
    ('mail2@yandex.ru', 'user2', 'Петров', '1975-10-09'),
    ('mail3@yandex.ru', 'user3', 'Сидоров', '1977-02-15');

MERGE INTO likes (film_id, user_id)
VALUES
    (2, 1),
    (2, 2);

MERGE INTO friends (user_id, friend_id)
VALUES
    (1, 2),
    (1, 3),
    (2, 1),
    (2, 3);
