package ru.yandex.practicum.filmorate.dal.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.FilmRepository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Repository("jdbcFilmRepository")
@RequiredArgsConstructor
public class JdbcFilmRepository implements FilmRepository {
    private final NamedParameterJdbcOperations jdbc;

    // region SQL queries

    private static final String FIND_ALL_QUERY = """
            SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id, m.name mpa_name,
                g.genre_id, g.name genre_name, d.director_id, d.name director_name
            FROM
                films f
            LEFT JOIN film_genres fg ON
                f.film_id = fg.film_id
            LEFT JOIN GENRES g ON
                fg.genre_id = g.genre_id
            LEFT JOIN mpa m ON
                f.mpa_id = m.mpa_id
            LEFT JOIN film_directors fd ON
                f.film_id = fd.film_id
            LEFT JOIN directors d ON
                fd.director_id = d.director_id""";
    private static final String GET_BY_ID_QUERY = """
            SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id, m.name mpa_name,
                g.genre_id, g.name genre_name, d.director_id, d.name director_name
            FROM
            	films f
            LEFT JOIN film_genres fg ON
            	f.film_id = fg.film_id
            LEFT JOIN genres g ON
            	fg.genre_id = g.genre_id
            LEFT JOIN mpa m ON
            	f.mpa_id = m.mpa_id
            LEFT JOIN film_directors fd ON
                f.film_id = fd.film_id
            LEFT JOIN directors d ON
                fd.director_id = d.director_id
            WHERE
            	f.film_id = :id""";
    private static final String INSERT_QUERY = """
            INSERT INTO films (name, description, release_date, duration, mpa_id)
            VALUES(:name, :desc, :rel_date, :duration, :mpa_id)""";
    private static final String UPDATE_QUERY = """
            UPDATE films
            SET name = :name, description = :desc, release_date = :rel_date, duration = :duration, mpa_id = :mpa_id
            WHERE film_id = :film_id""";
    private static final String INSERT_FILM_GENRES_QUERY =
            "MERGE INTO film_genres (film_id, genre_id) VALUES(:film_id, :genre_id)";
    private static final String DELETE_FILM_GENRES_QUERY =
            "DELETE FROM film_genres WHERE film_id = :film_id";
    private static final String INSERT_FILM_DIRECTOR_QUERY =
            "MERGE INTO film_directors (film_id, director_id) VALUES(:film_id, :director_id)";
    private static final String DELETE_FILM_DIRECTORS_QUERY =
            "DELETE FROM film_directors WHERE film_id = :film_id";
    private static final String ADD_LIKE_QUERY =
            "MERGE INTO likes (film_id, user_id) VALUES(:film_id, :user_id)";
    private static final String DELETE_LIKE_QUERY =
            "DELETE FROM likes WHERE film_id = :film_id AND user_id = :user_id";
    private static final String GET_TOP_FILMS = """
            SELECT fr.count, fr.film_id, fr.name, fr.description, fr.release_date, fr.duration, fr.mpa_id, m.name mpa_name,
            		g.genre_id, g.name genre_name, d.director_id, d.name director_name\s
            FROM (
            	SELECT COALESCE(gf.count, 0) count, f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id
            	FROM films f
            	LEFT JOIN (
            		SELECT film_id, COUNT(user_id) count
            		FROM
            			likes l
            		GROUP BY
            			film_id
            		) gf ON
            		f.film_id = gf.FILM_ID
            	ORDER BY count DESC
            	LIMIT :max_count) fr
            LEFT JOIN film_genres fg ON
            	fr.film_id = fg.film_id
            LEFT JOIN genres g ON
            	fg.genre_id = g.genre_id
            LEFT JOIN mpa m ON
            	fr.mpa_id = m.mpa_id
            LEFT JOIN film_directors fd ON
                fr.film_id = fd.film_id
            LEFT JOIN directors d ON
                fd.director_id = d.director_id""";
    private static final String GET_FILMS_BY_DIRECTOR_ID = """
            SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id, m.name mpa_name,
                g.genre_id, g.name genre_name, d.director_id, d.name director_name
            FROM
            	films f
            LEFT JOIN film_genres fg ON
            	f.film_id = fg.film_id
            LEFT JOIN genres g ON
            	fg.genre_id = g.genre_id
            LEFT JOIN mpa m ON
            	f.mpa_id = m.mpa_id
            LEFT JOIN film_directors fd ON
                f.film_id = fd.film_id
            LEFT JOIN directors d ON
                fd.director_id = d.director_id
            WHERE
            	d.director_id = :director_id
            ORDER BY
                f.release_date""";
    private static final String GET_DIRECTOR_FILMS_BY_LIKES = """
            SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id, m.name mpa_name,
                g.genre_id, g.name genre_name, d.director_id, d.name director_name
            FROM
                films AS f
            LEFT JOIN likes as l ON
                f.film_id = l.film_id
            LEFT JOIN mpa AS m ON
                f.mpa_id = m.mpa_id
            LEFT JOIN film_genres AS fg ON
                f.film_id = fg.film_id
            LEFT JOIN genres AS g ON
                fg.genre_id = g.genre_id
            LEFT JOIN film_directors AS fd ON
                f.film_id = fd.film_id
            LEFT JOIN directors AS d ON
                fd.director_id = d.director_id
            WHERE
                fd.director_id = :director_id
            GROUP BY
                f.film_id
            ORDER BY
                COUNT(l.film_id) DESC
            """;
    private static final String DELETE_FILM_BY_ID_QUERY = """
            DELETE FROM films
            WHERE film_id = :film_id""";
    private static final String DELETE_FILM_LIKES_QUERY = """
            DELETE FROM likes
            WHERE film_id = :film_id""";

    private static final String SEARCH_BY_TITLE_QUERY = """
            SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id, m.name mpa_name,
                g.genre_id, g.name genre_name, d.director_id, d.name director_name
            FROM films f
            LEFT JOIN film_genres fg ON f.film_id = fg.film_id
            LEFT JOIN genres g ON fg.genre_id = g.genre_id
            LEFT JOIN mpa m ON f.mpa_id = m.mpa_id
            LEFT JOIN film_directors fd ON f.film_id = fd.film_id
            LEFT JOIN directors d ON fd.director_id = d.director_id
            WHERE LOWER(f.name) LIKE LOWER(:query)
            ORDER BY (SELECT COUNT(*) FROM likes l WHERE l.film_id = f.film_id) DESC
            """;

    private static final String SEARCH_BY_DIRECTOR_QUERY = """
            SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id, m.name mpa_name,
                g.genre_id, g.name genre_name, d.director_id, d.name director_name
            FROM films f
            LEFT JOIN film_genres fg ON f.film_id = fg.film_id
            LEFT JOIN genres g ON fg.genre_id = g.genre_id
            LEFT JOIN mpa m ON f.mpa_id = m.mpa_id
            LEFT JOIN film_directors fd ON f.film_id = fd.film_id
            LEFT JOIN directors d ON fd.director_id = d.director_id
            WHERE LOWER(d.name) LIKE LOWER(:query)
            ORDER BY (SELECT COUNT(*) FROM likes l WHERE l.film_id = f.film_id) DESC
            """;

    private static final String SEARCH_BY_TITLE_AND_DIRECTOR_QUERY = """
            SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id, m.name mpa_name,
                g.genre_id, g.name genre_name, d.director_id, d.name director_name
            FROM films f
            LEFT JOIN film_genres fg ON f.film_id = fg.film_id
            LEFT JOIN genres g ON fg.genre_id = g.genre_id
            LEFT JOIN mpa m ON f.mpa_id = m.mpa_id
            LEFT JOIN film_directors fd ON f.film_id = fd.film_id
            LEFT JOIN directors d ON fd.director_id = d.director_id
            WHERE LOWER(f.name) LIKE LOWER(:query) OR LOWER(d.name) LIKE LOWER(:query)
            ORDER BY (SELECT COUNT(*) FROM likes l WHERE l.film_id = f.film_id) DESC
            """;
    private static final String GET_COMMON_FILMS_QUERY = """
            SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id, m.name mpa_name,
                g.genre_id, g.name genre_name, d.director_id, d.name director_name
            FROM (
            	SELECT l.film_id
            	FROM
            		likes l
                JOIN likes lk ON
                    lk.film_id = l.film_id
                WHERE l.user_id = :user_id AND lk.user_id = :friend_id
            	GROUP BY
            		l.film_id
            	ORDER BY
            		COUNT(l.user_id) DESC) gf
            JOIN films f ON
            	gf.film_id = f.FILM_ID
            LEFT JOIN film_genres fg ON
            	f.film_id = fg.film_id
            LEFT JOIN genres g ON
            	fg.genre_id = g.genre_id
            LEFT JOIN mpa m ON
            	f.mpa_id = m.mpa_id
            LEFT JOIN film_directors fd ON
                f.film_id = fd.film_id
            LEFT JOIN directors d ON
                fd.director_id = d.director_id
            """;

    private static final String GET_BY_USER_ID_QUERY = """
            SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id, m.name AS mpa_name,
                g.genre_id, g.name AS genre_name, d.director_id, d.name
            FROM
                films AS f
            INNER JOIN likes AS l ON
                f.film_id = l.film_id
            LEFT JOIN mpa AS m ON
                f.mpa_id = m.mpa_id
            LEFT JOIN film_genres AS fg ON
                f.film_id = fg.film_id
            LEFT JOIN genres AS g ON
                fg.genre_id = g.genre_id
            LEFT JOIN film_directors AS fd ON
                f.film_id = fd.film_id
            LEFT JOIN directors AS d ON
                fd.director_id = d.director_id
            WHERE
                l.user_id = :user_id""";

    private static final String GET_BY_USERS_IDS_QUERY = """
            SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id, m.name AS mpa_name,
                g.genre_id, g.name AS genre_name, d.director_id, d.name AS director_name
            FROM
                likes AS l
            INNER JOIN films AS f ON
                l.film_id = f.film_id
            LEFT JOIN mpa AS m ON
                f.mpa_id = m.mpa_id
            LEFT JOIN film_genres AS fg ON
                f.film_id = fg.film_id
            LEFT JOIN genres AS g ON
                fg.genre_id = g.genre_id
            LEFT JOIN film_directors AS fd ON
                f.film_id = fd.film_id
            LEFT JOIN directors AS d ON
                fd.director_id = d.director_id
            WHERE
                l.user_id IN (:users_ids)
            """;

    // endregion

    // region Mapper

    private static Film mapRowTo(ResultSet rs) throws SQLException {
        Date releaseDate = rs.getDate("release_date");
        return Film.builder()
                .id(rs.getInt("film_id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(releaseDate != null ? releaseDate.toLocalDate() : null)
                .duration(rs.getInt("duration"))
                .mpa(Mpa.builder()
                        .id(rs.getInt("mpa_id"))
                        .name(rs.getString("mpa_name"))
                        .build())
                .build();
    }

    private static Genre mapRowToGenre(ResultSet rs) throws SQLException {
        int genreId = rs.getInt("genre_id");
        if (rs.wasNull()) return null;

        return Genre.builder()
                .id(genreId)
                .name(rs.getString("genre_name"))
                .build();
    }

    private static Director mapRowToDirector(ResultSet rs) throws SQLException {
        int directorId = rs.getInt("director_id");
        if (rs.wasNull()) return null;

        return Director.builder()
                .id(directorId)
                .name(rs.getString("director_name"))
                .build();
    }

    private static Film mapSetToOne(ResultSet rs) throws SQLException {
        Film film = null;
        while (rs.next()) {
            Integer filmId = rs.getInt("film_id");
            if (film == null) {
                film = mapRowTo(rs);
                film.setGenres(new LinkedHashSet<>());
                film.setDirectors(new LinkedHashSet<>());
            }
            if (!Objects.equals(filmId, film.getId())) break;
            Genre genre = mapRowToGenre(rs);
            if (genre != null) {
                film.getGenres().add(genre);
            }
            Director director = mapRowToDirector(rs);
            if (director != null) {
                film.getDirectors().add(director);
            }
        }
        return film;
    }

    private static List<Film> mapSetToList(ResultSet rs) throws SQLException {
        LinkedHashMap<Integer, Film> films = new LinkedHashMap<>();
        while (rs.next()) {
            Integer filmId = rs.getInt("film_id");
            Film film = films.get(filmId);
            if (film == null) {
                film = mapRowTo(rs);
                films.put(film.getId(), film);
                film.setGenres(new LinkedHashSet<>());
                film.setDirectors(new LinkedHashSet<>());
            }
            Genre genre = mapRowToGenre(rs);
            if (genre != null) {
                film.getGenres().add(genre);
            }
            Director director = mapRowToDirector(rs);
            if (director != null) {
                film.getDirectors().add(director);
            }
        }
        return new ArrayList<>(films.values());
    }

    private static Set<Integer> mapSetToFilmIds(ResultSet rs) throws SQLException {
        Set<Integer> filmsIds = new HashSet<>();

        while (rs.next()) {
            Integer filmId = rs.getInt("film_id");
            filmsIds.add(filmId);
        }
        return filmsIds;
    }

    // endregion

    @Override
    public Optional<Film> get(Integer id) {
        if (id == null) return Optional.empty();

        return Optional.ofNullable(jdbc.query(GET_BY_ID_QUERY,
                new MapSqlParameterSource("id", id),
                JdbcFilmRepository::mapSetToOne));
    }

    @Override
    public List<Film> findAll() {
        return jdbc.query(FIND_ALL_QUERY,
                JdbcFilmRepository::mapSetToList);
    }

    @Override
    public Film create(Film film) {
        GeneratedKeyHolder gkh = new GeneratedKeyHolder();
        Mpa mpa = film.getMpa();
        jdbc.update(INSERT_QUERY,
                new MapSqlParameterSource("name", film.getName())
                        .addValue("desc", film.getDescription())
                        .addValue("rel_date", film.getReleaseDate())
                        .addValue("duration", film.getDuration())
                        .addValue("mpa_id", mpa != null ? mpa.getId() : null),
                gkh);
        film.setId(gkh.getKeyAs(Integer.class));
        // Создать связи фильм - жанры
        insertFilmGenres(film.getId(), film.getGenres());
        // Создать связи фильм - режиссер
        insertFilmDirectors(film.getId(), film.getDirectors());
        return film;
    }

    private void insertFilmGenres(int filmId, Set<Genre> genres) {
        if (genres == null || genres.isEmpty()) return;

        MapSqlParameterSource[] batchArgs = genres.stream()
                .map(g -> new MapSqlParameterSource("film_id", filmId)
                        .addValue("genre_id", g.getId()))
                .toArray(MapSqlParameterSource[]::new);
        jdbc.batchUpdate(INSERT_FILM_GENRES_QUERY, batchArgs);
    }

    @Override
    public void deleteFilmGenres(int filmId) {
        jdbc.update(DELETE_FILM_GENRES_QUERY, new MapSqlParameterSource("film_id", filmId));
    }

    private void insertFilmDirectors(int filmId, Set<Director> directors) {
        if (directors == null || directors.isEmpty()) return;

        MapSqlParameterSource[] batchArgs = directors.stream()
                .map(d -> new MapSqlParameterSource("film_id", filmId)
                        .addValue("director_id", d.getId()))
                .toArray(MapSqlParameterSource[]::new);
        jdbc.batchUpdate(INSERT_FILM_DIRECTOR_QUERY, batchArgs);
    }

    @Override
    public void deleteFilmDirectors(int filmId) {
        jdbc.update(DELETE_FILM_DIRECTORS_QUERY, new MapSqlParameterSource("film_id", filmId));
    }

    @Override
    public Film update(Film newFilm) {
        Mpa mpa = newFilm.getMpa();
        jdbc.update(UPDATE_QUERY,
                new MapSqlParameterSource("name", newFilm.getName())
                        .addValue("desc", newFilm.getDescription())
                        .addValue("rel_date", newFilm.getReleaseDate())
                        .addValue("duration", newFilm.getDuration())
                        .addValue("mpa_id", mpa != null ? mpa.getId() : null)
                        .addValue("film_id", newFilm.getId()));
        // Удалить старые связи фильм - жанры
        deleteFilmGenres(newFilm.getId());
        // Создать связи фильм - жанры
        insertFilmGenres(newFilm.getId(), newFilm.getGenres());
        // Удалить старые связи фильм - режиссер
        deleteFilmDirectors(newFilm.getId());
        // Создать связи фильм - режиссер
        insertFilmDirectors(newFilm.getId(), newFilm.getDirectors());
        return newFilm;
    }

    @Override
    public void addLike(Film film, User user) {
        jdbc.update(ADD_LIKE_QUERY,
                new MapSqlParameterSource("film_id", film.getId())
                        .addValue("user_id", user.getId()));
    }

    @Override
    public void deleteLike(Film film, User user) {
        jdbc.update(DELETE_LIKE_QUERY,
                new MapSqlParameterSource("film_id", film.getId())
                        .addValue("user_id", user.getId()));
    }

    @Override
    public List<Film> getTopFilms(int maxCount) {
        return jdbc.query(GET_TOP_FILMS,
                new MapSqlParameterSource("max_count", maxCount),
                JdbcFilmRepository::mapSetToList);
    }

    @Override
    public List<Film> getTopFilms(int count, Integer genreId, Integer year) {
        //WHERE g.genre_id = 1 AND YEAR(f.release_date) = 2003
        ArrayList<String> predicates = new ArrayList<>();
        if (genreId != null) {
            predicates.add("fr.film_id IN (SELECT film_id FROM film_genres WHERE genre_id = :genreId)");
        }
        if (year != null) {
            predicates.add("YEAR(fr.release_date) = :year");
        }

        String predicate = "\nWHERE " + String.join(" AND ", predicates);
        return jdbc.query(GET_TOP_FILMS
                        + predicate,
                new MapSqlParameterSource("max_count", count)
                        .addValue("genreId", genreId)
                        .addValue("year", year),
                JdbcFilmRepository::mapSetToList);
    }

    @Override
    public List<Film> getFilmsByDirector(Integer directorId) {
        if (directorId == null) return null;

        return jdbc.query(GET_FILMS_BY_DIRECTOR_ID,
                new MapSqlParameterSource("director_id", directorId),
                JdbcFilmRepository::mapSetToList);
    }

    @Override
    public List<Film> getDirectorFilmsByLikes(Integer directorId) {
        if (directorId == null) return null;

        return jdbc.query(GET_DIRECTOR_FILMS_BY_LIKES,
                new MapSqlParameterSource("director_id", directorId),
                JdbcFilmRepository::mapSetToList);
    }

    @Override
    public void deleteFilmById(int filmId) {
        jdbc.update(DELETE_FILM_BY_ID_QUERY,
                new MapSqlParameterSource("film_id", filmId));
    }

    @Override
    public void deleteFilmLikes(int filmId) {
        jdbc.update(DELETE_FILM_LIKES_QUERY,
                new MapSqlParameterSource("film_id", filmId));
    }

    @Override
    public List<Film> searchFilms(String query, String by) {
        String sql;
        if ("title".equalsIgnoreCase(by)) {
            sql = SEARCH_BY_TITLE_QUERY;
        } else if ("director".equalsIgnoreCase(by)) {
            sql = SEARCH_BY_DIRECTOR_QUERY;
        } else if ("title,director".equalsIgnoreCase(by) || "director,title".equalsIgnoreCase(by)) {
            sql = SEARCH_BY_TITLE_AND_DIRECTOR_QUERY;
        } else {
            throw new IllegalArgumentException("Invalid search parameter: " + by);
        }

        return jdbc.query(sql, new MapSqlParameterSource("query", "%" + query + "%"), JdbcFilmRepository::mapSetToList);
    }

    @Override
    public List<Film> getCommonFilms(User user, User friend) {
        return jdbc.query(GET_COMMON_FILMS_QUERY,
                new MapSqlParameterSource("user_id", user.getId())
                        .addValue("friend_id", friend.getId()),
                JdbcFilmRepository::mapSetToList);
    }

    @Override
    public Set<Integer> getFilmsLikedByUser(int userId) {

        return jdbc.query(GET_BY_USER_ID_QUERY,
                new MapSqlParameterSource("user_id", userId),
                JdbcFilmRepository::mapSetToFilmIds);
    }

    @Override
    public List<Film> getLikedFilmsByUsersIds(Set<Integer> usersIds) {

        return jdbc.query(GET_BY_USERS_IDS_QUERY,
                new MapSqlParameterSource()
                        .addValue("users_ids", usersIds),
                JdbcFilmRepository::mapSetToList);

    }
}
