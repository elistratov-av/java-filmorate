# java-filmorate
## Диаграмма БД
![Схема БД приложения Filmorate](/assets/images/Filmorate.png)
**users**

Содежит данные о пользователях.
Таблица включает такие поля:
- первичный ключ __user_id__ - идентификатор пользователя
- _email_ - эл. почта
- _login_ - логин
- _user_name_ - имя пользователя
- _birthday_ - дата рождения

**films**

Содержит данные о фильмах.
Таблица включает такие поля:
- первичный ключ __film_id__ - идентификатор фильма
- _name_ - название
- _description_ - описание
- _release_date_ - дата выхода
- _duration_ - продолжительность (в часах)
- _mpa_id_ - ссылка на справочник **mpa** - возрастной рейтинг фильмов
  
**mpa**

Содержит данные о возрастных рейтингах для фильмов по классификации MPA:
+ G — у фильма нет возрастных ограничений,
+ PG — детям рекомендуется смотреть фильм с родителями,
+ PG-13 — детям до 13 лет просмотр не желателен,
+ R — лицам до 17 лет просматривать фильм можно только в присутствии взрослого,
+ NC-17 — лицам до 18 лет просмотр запрещён.

Таблица включает такие поля:
- первичный ключ _mpa_id_ - идентификатор возрастного рейтинга
- _name_ - название рейтинга
- _description_ - описание рейтинга

**genres**

Содержит данные о жанрах.
Таблица включает такие поля:
- первичный ключ _genre_id_ - идентификатор жанра
- _name_ - название

**friends**

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
INSERT INTO users (email, login, user_name, birthday)
VALUES ('mail1@yandex.ru', 'user1', 'Петров', '1972-08-03');
```
Добавление лайка:
```dbn-psql
MERGE INTO likes (film_id, user_id) VALUES(1, 1);
```
Добавление в друзья (подтвержденная связь):
```dbn-psql
MERGE INTO friends (user_id, friend_id) VALUES(1, 4);
MERGE INTO friends (user_id, friend_id) VALUES(4, 1);
```
Выборка друзей пользователя:
```dbn-psql
SELECT u.user_id, u.email, u.login, u.user_name, u.birthday
FROM
	friends fr
JOIN users u ON
	fr.friend_id = u.user_id
WHERE
	fr.user_id = 2;
```
Выборка общих друзей у двух пользователей:
```dbn-psql
SELECT u.user_id, u.email, u.login, u.user_name, u.birthday
FROM
	friends fr
JOIN users u ON
	fr.friend_id = u.user_id
WHERE
	fr.user_id = 1
	AND fr.friend_id IN (
	SELECT
		fr3.friend_id
	FROM
		friends fr3
	WHERE
		fr3.user_id = 3);
```
Выборка топ 10 популярных фильмов:
```dbn-psql
SELECT gf.count, f.*, m.name mpa_name, g.genre_id, g.name genre_name
FROM (
    SELECT film_id, COUNT(user_id) count
    FROM
        likes l
    GROUP BY
        film_id
    ORDER BY
        COUNT(user_id) DESC
    LIMIT 10) gf
JOIN films f ON
    gf.film_id = f.FILM_ID
LEFT JOIN film_genres fg ON
    f.film_id = fg.film_id
LEFT JOIN genres g ON
    fg.genre_id = g.genre_id
LEFT JOIN mpa m ON
    f.mpa_id = m.mpa_id;
```
Вставка фильма:
```dbn-psql
INSERT INTO films (name, description, release_date, duration, mpa_id)
VALUES ('Фильм 1', 'Интересный фильм', '2019-05-14', 1.5, 1);
```
Удаление фильма по идентификатору:
```dbn-psql
DELETE FROM films WHERE film_id = 6;
```
Вставка жанров:
```dbn-psql
MERGE INTO genres (name)
VALUES
    (1, 'Комедия'),
    (2, 'Драма'),
    (3, 'Мультфильм'),
    (4, 'Триллер'),
    (5, 'Документальный'),
    (6, 'Боевик');
```
Добавление жанра к фильму:
```dbn-psql
MERGE INTO film_genres (film_id, genre_id) VALUES(1, 2);
```
