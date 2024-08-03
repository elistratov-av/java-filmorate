# java-filmorate
## Диаграмма БД
![Схема БД приложения Filmorate](/assets/images/Filmorate.png)
**users**

Содежит данные о пользователях.
Таблица включает такие поля:
- первичный ключ __user_id__ - идентификатор пользователя
- _email_ - эл. почта
- _login_ - логин
- _name_ - имя пользователя
- _birthday_ - дата рождения

**films**

Содержит данные о фильмах.
Таблица включает такие поля:
- первичный ключ __film_id__ - идентификатор фильма
- _name_ - название
- _description_ - описание
- _release_date_ - дата выхода
- _duration_ - продолжительность (в часах)
- _rating_ - возрастной рейтинг по классификации MPA
  + G — у фильма нет возрастных ограничений,
  + PG — детям рекомендуется смотреть фильм с родителями,
  + PG13 — детям до 13 лет просмотр не желателен,
  + R — лицам до 17 лет просматривать фильм можно только в присутствии взрослого,
  + NC17 — лицам до 18 лет просмотр запрещён.

**genres**

Содержит данные о жанрах.
Таблица включает такие поля:
- первичный ключ _genre_id_ - идентификатор жанра
- _name_ - название

**friendship**

Содержит информацию о дружеских связях между пользователями.
Дружба считается подтвержденной, если в таблице присутствует двунаправленная связь.
Таблица включает такие поля:
- сост. первичный ключ _user_id_ - идентификатор пользователя
- сост. первичный ключ _friend_id_ - идентификатор друга (пользователя)

**likes**

Содержит информацию о лайках, проставленных пользователями к фильмам.
Таблица включает такие поля:
- сост. первичный ключ _film_id_ - идентификатор фильма
- сост. первичный ключ _user_id_ - идентификатор пользователя, поставившего лайк

**film_genres**

Содежит информацию о том к каким жанрам относится фильм.
Таблица включает такие поля:
- сост. первичный ключ _film_id_ - идентификатор фильма
- сост. первичный ключ _genre_id_ - идентификатор жанра

## Типичные запросы к БД
Выборка всех пользователей:
```dbn-psql
SELECT * FROM users;
```
Вставка нового пользователя:
```dbn-psql
INSERT INTO users (email, login, "name", birthday)
VALUES('mail1@yandex.ru', 'user1', 'Петров', '1972-08-03');
```
Добавление лайка:
```dbn-psql
INSERT INTO likes (film_id, user_id) VALUES(1, 1);
```
Добавление в друзья (подтвержденная связь):
```dbn-psql
INSERT INTO friendship (user_id, friend_id) VALUES(1, 4);
INSERT INTO friendship (user_id, friend_id) VALUES(4, 1);
```
Выборка друзей пользователя (во втором запросе только подтвержденные друзья):
```dbn-psql
SELECT
	u.user_id,
	u.email,
	u.login,
	u."name" user_name,
	u.birthday
FROM
	friendship fr
JOIN users u ON
	fr.friend_id = u.user_id
WHERE
	fr.user_id = 2;

SELECT
	u.user_id,
	u.email,
	u.login,
	u."name" user_name,
	u.birthday
FROM
	friendship fr
JOIN friendship fr2 ON
	fr.friend_id = fr2.user_id AND fr.user_id = fr2.friend_id
JOIN users u ON
	fr.friend_id = u.user_id
WHERE
	fr.user_id = 1;
```
Выборка общих друзей у двух пользователей (аналогично предыдущему, второй запрос для подтвержденных друзей):
```dbn-psql
SELECT
	u.user_id,
	u.email,
	u.login,
	u."name" user_name,
	u.birthday
FROM
	friendship fr
JOIN users u ON
	fr.friend_id = u.user_id
WHERE
	fr.user_id = 1
	AND fr.friend_id IN (
	SELECT
		fr3.friend_id
	FROM
		friendship fr3
	WHERE
		fr3.user_id = 3);

SELECT
	u.user_id,
	u.email,
	u.login,
	u."name" user_name,
	u.birthday
FROM
	friendship fr
JOIN friendship fr2 ON
	fr.friend_id = fr2.user_id AND fr.user_id = fr2.friend_id
JOIN users u ON
	fr.friend_id = u.user_id
WHERE
	fr.user_id = 1
	AND fr.friend_id IN (
	SELECT
		fr3.friend_id
	FROM
		friendship fr3
	JOIN friendship fr4 ON
		fr3.friend_id = fr4.user_id AND fr3.user_id = fr4.friend_id
	WHERE
		fr3.user_id = 3);
```
Выборка топ 10 популярных фильмов:
```dbn-psql
SELECT
	f.film_id,
	f."name" film_name,
	release_date,
	duration,
	rating,
	g."name" genre
FROM
	films f
JOIN film_genres fg ON
	f.film_id = fg.film_id
JOIN genres g ON
	fg.genre_id = g.genre_id
WHERE
	f.film_id IN (
	SELECT
		film_id
	FROM
		likes l
	GROUP BY
		film_id
	ORDER BY
		COUNT(user_id) DESC
	LIMIT 10);
```
Вставка фильма:
```dbn-psql
INSERT INTO films ("name", description, release_date, duration, rating)
VALUES('Фильм 1', 'Интересный фильм', '2019-05-14', 1.5, 'G');
```
Удаление фильма по идентификатору:
```dbn-psql
DELETE FROM films WHERE film_id=6;
```
Вставка жанров:
```dbn-psql
INSERT INTO genres ("name") VALUES('Комедия');
INSERT INTO genres ("name") VALUES('Драма');
INSERT INTO genres ("name") VALUES('Мультфильм');
INSERT INTO genres ("name") VALUES('Триллер');
INSERT INTO genres ("name") VALUES('Документальный');
INSERT INTO genres ("name") VALUES('Боевик');
```
Добавление жанра к фильму:
```dbn-psql
INSERT INTO film_genres (film_id, genre_id) VALUES(1, 2);
```
