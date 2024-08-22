package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JdbcMpaRepository implements MpaRepository {
    private final NamedParameterJdbcOperations jdbc;

    // region SQL queries

    private static final String GET_BY_ID_QUERY = "SELECT * FROM mpa m WHERE m.mpa_id = :id";
    private static final String FIND_ALL_QUERY = "SELECT * FROM mpa";

    // endregion

    // region Mapper

    private static Mpa mapRowTo(ResultSet rs, int rowNum) throws SQLException {
        return Mpa.builder()
                .id(rs.getInt("mpa_id"))
                .name(rs.getString("name"))
                .build();
    }

    // endregion

    @Override
    public Optional<Mpa> get(Integer id) {
        try {
            if (id == null) return Optional.empty();

            return Optional.ofNullable(jdbc.queryForObject(GET_BY_ID_QUERY,
                    new MapSqlParameterSource("id", id),
                    JdbcMpaRepository::mapRowTo));
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    @Override
    public List<Mpa> findAll() {
        return jdbc.query(FIND_ALL_QUERY, JdbcMpaRepository::mapRowTo);
    }
}
