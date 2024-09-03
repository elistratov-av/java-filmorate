package ru.yandex.practicum.filmorate.dal.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.ReviewRepository;

@Repository
@RequiredArgsConstructor
public class JdbcReviewRepository implements ReviewRepository {
    private final NamedParameterJdbcOperations jdbc;

    // region SQL queries

    //private static final String GET_BY_ID_QUERY = "SELECT mpa_id, name FROM mpa m WHERE m.mpa_id = :id";

    // endregion

    // region Mapper


    // endregion
}
