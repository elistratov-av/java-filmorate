package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
            SELECT f.*, m.name mpa_name, g.genre_id, g.name genre_name
            FROM
            	films f
            LEFT JOIN film_genres fg ON
            	f.film_id = fg.film_id
            LEFT JOIN GENRES g ON
            	fg.genre_id = g.genre_id
            LEFT JOIN mpa m ON
            	f.mpa_id = m.mpa_id""";
    private static final String GET_BY_ID_QUERY = """
            SELECT f.*, m.name mpa_name, g.genre_id, g.name genre_name
            FROM
            	films f
            LEFT JOIN film_genres fg ON
            	f.film_id = fg.film_id
            LEFT JOIN genres g ON
            	fg.genre_id = g.genre_id
            LEFT JOIN mpa m ON
            	f.mpa_id = m.mpa_id
            WHERE
            	f.film_id = :id""";
    private static final String INSERT_QUERY = "INSERT INTO films (name, description, release_date, duration, mpa_id) VALUES(:name, :desc, :rel_date, :duration, :mpa_id)";
    private static final String UPDATE_QUERY = "UPDATE films SET name = :name, description = :desc, release_date = :rel_date, duration = :duration, mpa_id = mpa_id WHERE film_id = :film_id";
    private static final String INSERT_FILM_GENRES_QUERY = "MERGE INTO film_genres (film_id, genre_id) VALUES(:film_id, :genre_id)";
    private static final String DELETE_FILM_GENRES_QUERY = "DELETE FROM film_genres WHERE film_id = :film_id";
    private static final String ADD_LIKE_QUERY = "MERGE INTO likes (film_id, user_id) VALUES(:film_id, :user_id)";
    private static final String DELETE_LIKE_QUERY = "DELETE FROM likes WHERE film_id = :film_id AND user_id = :user_id";
    private static final String GET_TOP_FILMS = """
            SELECT gf.count, f.*, m.name mpa_name, g.genre_id, g.name genre_name
            FROM (
            	SELECT film_id, COUNT(user_id) count
            	FROM
            		likes l
            	GROUP BY
            		film_id
            	ORDER BY
            		COUNT(user_id) DESC
            	LIMIT :max_count) gf
            JOIN films f ON
            	gf.film_id = f.FILM_ID
            LEFT JOIN film_genres fg ON
            	f.film_id = fg.film_id
            LEFT JOIN genres g ON
            	fg.genre_id = g.genre_id
            LEFT JOIN mpa m ON
            	f.mpa_id = m.mpa_id""";

    // endregion

    // region Mapper

    private static Film mapRowTo(ResultSet rs) throws SQLException {
        Date releaseDate = rs.getDate("release_date");
        return Film.builder()
                .id(rs.getInt("film_id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(releaseDate != null ? releaseDate.toLocalDate() : null)
                .duration(rs.getDouble("duration"))
                .mpa(Mpa.builder()
                        .id(rs.getInt("mpa_id"))
                        .name(rs.getString("mpa_name"))
                        .build())
                .build();
    }

    private static Genre mapRowToGenre(ResultSet rs) throws SQLException {
        return Genre.builder()
                .id(rs.getInt("genre_id"))
                .name(rs.getString("genre_name"))
                .build();
    }

    private static Film mapSetToOne(ResultSet rs) throws SQLException {
        Film film = null;
        while (rs.next()) {
            Integer filmId = rs.getInt("film_id");
            if (film == null) {
                film = mapRowTo(rs);
                film.setGenres(new LinkedHashSet<>());
            }
            if (!Objects.equals(filmId, film.getId())) break;
            film.getGenres().add(mapRowToGenre(rs));
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
                film.setGenres(new LinkedHashSet<>());
                films.put(film.getId(), film);
            }
            film.getGenres().add(mapRowToGenre(rs));
        }
        return new ArrayList<>(films.values());
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

    private void deleteFilmGenres(int filmId) {
        jdbc.update(DELETE_FILM_GENRES_QUERY, new MapSqlParameterSource("film_id", filmId));
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
}
