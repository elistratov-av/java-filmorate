package ru.yandex.practicum.filmorate.dal.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.FeedRepository;
import ru.yandex.practicum.filmorate.model.Feed;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class JdbcFeedRepository implements FeedRepository {
    private final NamedParameterJdbcOperations jdbc;

    // region SQL queries

    private static final String INSERT_QUERY = """
            INSERT INTO feed (user_id, entity_id, timestamp, event_type, operation)
            VALUES(:user_id, :entity_id, :timestamp, :event_type, :operation)""";
    private static final String SELECT_BY_USER_ID_QUERY = """
            SELECT timestamp, user_id, event_type, operation, event_id, entity_id
            FROM feed
            WHERE user_id = :user_id
            ORDER BY event_id""";
    private static final String DELETE_USER_FEED_QUERY = """
            DELETE FROM feed
            WHERE user_id = :user_id OR (event_type = 'FRIEND' AND entity_id = :user_id)""";
    private static final String DELETE_FILM_FEED_QUERY = """
            DELETE FROM feed
            WHERE event_type IN ('LIKE', 'REVIEW') AND entity_id = :film_id""";
    // endregion

    // region Mapper

    private static Feed mapRowTo(ResultSet rs, int rowNum) throws SQLException {
        return Feed.builder()
                .eventId(rs.getInt("event_id"))
                .userId(rs.getInt("user_id"))
                .entityId(rs.getInt("entity_id"))
                .timestamp(rs.getTimestamp("timestamp").getTime())
                .eventType(Feed.EventType.valueOf(rs.getString("event_type")))
                .operation(Feed.Operation.valueOf(rs.getString("operation")))
                .build();
    }

    // endregion

    @Override
    public Feed create(Feed feed) {
        GeneratedKeyHolder gkh = new GeneratedKeyHolder();
        jdbc.update(INSERT_QUERY,
                new MapSqlParameterSource("user_id", feed.getUserId())
                        .addValue("entity_id", feed.getEntityId())
                        .addValue("timestamp", new Timestamp(feed.getTimestamp()))
                        .addValue("event_type", feed.getEventType().name())
                        .addValue("operation", feed.getOperation().name()),
                gkh);
        feed.setEventId(gkh.getKeyAs(Integer.class));
        return feed;
    }

    @Override
    public List<Feed> findFeedByUserId(Integer id) {
        return jdbc.query(SELECT_BY_USER_ID_QUERY,
                new MapSqlParameterSource("user_id", id),
                JdbcFeedRepository::mapRowTo);
    }

    @Override
    public void deleteUserFeed(int userId) {
        jdbc.update(DELETE_USER_FEED_QUERY,
                new MapSqlParameterSource("user_id", userId));
    }

    @Override
    public void deleteFilmFeed(int filmId) {
        jdbc.update(DELETE_FILM_FEED_QUERY,
                new MapSqlParameterSource("film_id", filmId));
    }
}
