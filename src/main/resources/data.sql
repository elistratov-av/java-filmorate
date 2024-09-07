MERGE INTO mpa (mpa_id, name, description)
VALUES
	(1, 'G', 'у фильма нет возрастных ограничений'),
	(2, 'PG', 'детям рекомендуется смотреть фильм с родителями'),
	(3, 'PG-13', 'детям до 13 лет просмотр не желателен'),
	(4, 'R', 'лицам до 17 лет просматривать фильм можно только в присутствии взрослого'),
	(5, 'NC-17', 'лицам до 18 лет просмотр запрещён');

MERGE INTO genres (genre_id, name)
VALUES
	(1, 'Комедия'),
	(2, 'Драма'),
	(3, 'Мультфильм'),
	(4, 'Триллер'),
	(5, 'Документальный'),
	(6, 'Боевик');

INSERT INTO users (email, login, user_name, birthday)
VALUES
    ('test@mail.ru', 'test', 'test', '2000-01-01'),
    ('test2@mail.ru', 'test2', 'test2', '2000-01-01');

INSERT INTO feed (user_id, entity_id, timestamp, event_type, operation)
VALUES
    (1, 1, TIMESTAMP '2005-12-31 23:59:59', 'LIKE', 'ADD'),
    (2, 2, TIMESTAMP '2005-12-31 23:59:59', 'FRIEND', 'DELETE');

INSERT INTO films (name, description, release_date)
VALUES
    ('test', 'test', '2000-01-01'),
    ('test2', 'test2', '2000-01-01');
