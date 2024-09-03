package ru.yandex.practicum.filmorate.dal.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.FeedRepository;

@Repository
@RequiredArgsConstructor
public class JdbcFeedRepository implements FeedRepository {
    private final NamedParameterJdbcOperations jdbc;

    // region SQL queries

    //private static final String GET_BY_ID_QUERY = "SELECT mpa_id, name FROM mpa m WHERE m.mpa_id = :id";

    // endregion

    // region Mapper


    // endregion
}
