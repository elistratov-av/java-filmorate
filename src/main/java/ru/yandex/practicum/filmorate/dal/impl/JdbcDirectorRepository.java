package ru.yandex.practicum.filmorate.dal.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.dal.DirectorRepository;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JdbcDirectorRepository implements DirectorRepository {
    private final NamedParameterJdbcOperations jdbc;

    // region SQL queries

    private static final String FIND_ALL_QUERY = "SELECT director_id, name FROM directors";

    private static final String GET_BY_ID_QUERY = "SELECT director_id, name FROM directors WHERE director_id = :id";

    private static final String GET_BY_IDS_QUERY = "SELECT director_id, name FROM directors WHERE director_id IN (:ids)";

    private static final String INSERT_QUERY = "INSERT INTO directors (name) VALUES (:name)";

    private static final String UPDATE_QUERY = "UPDATE directors SET name = :name WHERE director_id = :id";

    private static final String DELETE_FILM_DIRECTORS_QUERY = "DELETE FROM film_directors WHERE director_id = :id";

    private static final String DELETE_QUERY = "DELETE FROM directors WHERE director_id = :id";

    // endregion

    // region Mapper

    private static Director mapRowTo(ResultSet rs, int rowNum) throws SQLException {
        return Director.builder()
                .id(rs.getInt("director_id"))
                .name(rs.getString("name"))
                .build();
    }

    // endregion

    @Override
    public List<Director> findAll() {
        return jdbc.query(FIND_ALL_QUERY,
                JdbcDirectorRepository::mapRowTo);
    }

    @Override
    public List<Director> getByIds(List<Integer> ids) {
        return jdbc.query(GET_BY_IDS_QUERY,
                new MapSqlParameterSource("ids", ids),
                JdbcDirectorRepository::mapRowTo);
    }

    @Override
    public Optional<Director> get(Integer id) {
        try {
            if (id == null) return Optional.empty();

            return Optional.ofNullable(jdbc.queryForObject(GET_BY_ID_QUERY,
                    new MapSqlParameterSource("id", id),
                    JdbcDirectorRepository::mapRowTo));
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    @Override
    public Director create(Director director) {
        GeneratedKeyHolder gkh = new GeneratedKeyHolder();
        jdbc.update(INSERT_QUERY,
                new MapSqlParameterSource("name", director.getName()),
                gkh);
        director.setId(gkh.getKeyAs(Integer.class));
        return director;
    }

    @Override
    public Director update(Director director) {
        jdbc.update(UPDATE_QUERY,
                new MapSqlParameterSource("name", director.getName())
                        .addValue("id", director.getId()));
        return director;
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        MapSqlParameterSource params = new MapSqlParameterSource("id", id);
        jdbc.update(DELETE_FILM_DIRECTORS_QUERY, params);
        jdbc.update(DELETE_QUERY, params);
    }
}
