package ru.yandex.practicum.filmorate.dal.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.ReviewRepository;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JdbcReviewRepository implements ReviewRepository {
    private final NamedParameterJdbcOperations jdbc;

    // region SQL queries

    private static final String GET_BY_ID_QUERY = """
            SELECT review_id, content, is_positive, film_id, user_id, useful
            FROM reviews
            WHERE review_id = :id""";
    private static final String FIND_ALL_QUERY = """
            SELECT review_id, content, is_positive, film_id, user_id, useful
            FROM reviews""";
    private static final String INSERT_QUERY = """
            INSERT INTO reviews (content, is_positive, film_id, user_id)
            VALUES(:content, :is_positive, :film_id, :user_id)""";
    private static final String UPDATE_QUERY = """
            UPDATE reviews
            SET content = :content, is_positive = :is_positive
            WHERE review_id = :review_id""";
    private static final String DELETE_BY_ID_QUERY = "DELETE FROM reviews WHERE review_id = :review_id";
    private static final String DELETE_LIKES_BY_REVIEW_QUERY = "DELETE FROM review_likes WHERE review_id = :review_id";
    private static final String ADD_LIKE_QUERY =
            "MERGE INTO review_likes (review_id, user_id, is_like) values(:review_id, :user_id, :is_like)";
    private static final String DELETE_LIKE_QUERY = """
            DELETE FROM review_likes
            WHERE review_id = :review_id AND user_id = :user_id AND is_like = :is_like""";
    private static final String REFRESH_RATING_QYERY = """
            UPDATE reviews
            SET useful = COALESCE ((
                SELECT COUNT(CASE WHEN is_like THEN 1 END) - COUNT(CASE WHEN NOT is_like THEN 1 END) rating
                FROM review_likes
                GROUP BY review_id
                HAVING review_id = :review_id), 0)
            WHERE review_id = :review_id""";
    private static final String DELETE_REVIEW_LIKES_BY_FILM_REVIEW_QUERY = """
            DELETE FROM review_likes
            WHERE review_id IN (
            	SELECT review_id
            	FROM reviews r
            	WHERE r.film_id = :film_id)""";
    private static final String DELETE_FILM_REVIEWS_QUERY = """
            DELETE FROM reviews
            WHERE film_id = :film_id""";
    private static final String DELETE_REVIEW_LIKES_BY_USER_REVIEW_QUERY = """
            DELETE FROM review_likes
            WHERE review_id IN (
            	SELECT review_id
            	FROM reviews r
            	WHERE r.user_id = :user_id)""";
    private static final String DELETE_USER_REVIEWS_QUERY = """
            DELETE FROM REVIEWS
            WHERE USER_ID = :user_id""";
    private static final String FIND_USER_REVIEW_LIKES_QUERY = """
            SELECT review_id
            FROM review_likes
            WHERE user_id = :user_id""";
    private static final String DELETE_REVIEW_LIKES_BY_USER_QUERY = """
            DELETE FROM REVIEW_LIKES
            WHERE USER_ID = :user_id""";

    // endregion

    // region Mapper

    private static Review mapRowTo(ResultSet rs, int rowNum) throws SQLException {
        Review review = new Review();
        review.setReviewId(rs.getInt("review_id"));
        review.setContent(rs.getString("content"));
        review.setIsPositive(rs.getBoolean("is_positive"));
        review.setFilmId(rs.getInt("film_id"));
        review.setUserId(rs.getInt("user_id"));
        review.setUseful(rs.getInt("useful"));
        return review;
    }

    // endregion

    @Override
    public Optional<Review> get(Integer id) {
        try {
            if (id == null) return Optional.empty();

            return Optional.ofNullable(jdbc.queryForObject(GET_BY_ID_QUERY,
                    new MapSqlParameterSource("id", id),
                    JdbcReviewRepository::mapRowTo));
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    @Override
    public List<Review> findByFilmId(int filmId, int count) {
        String query = FIND_ALL_QUERY + "\nWHERE film_id = :film_id\nORDER BY useful DESC\nLIMIT :count";
        return jdbc.query(query,
                new MapSqlParameterSource("film_id", filmId)
                        .addValue("count", count),
                JdbcReviewRepository::mapRowTo);
    }

    @Override
    public List<Review> findAll(int count) {
        String query = FIND_ALL_QUERY + "\nORDER BY useful DESC\nLIMIT :count";
        return jdbc.query(query,
                new MapSqlParameterSource("count", count),
                JdbcReviewRepository::mapRowTo);
    }

    @Override
    public Review create(Review review) {
        GeneratedKeyHolder gkh = new GeneratedKeyHolder();
        jdbc.update(INSERT_QUERY,
                new MapSqlParameterSource("content", review.getContent())
                        .addValue("is_positive", review.getIsPositive())
                        .addValue("film_id", review.getFilmId())
                        .addValue("user_id", review.getUserId()),
                gkh);
        review.setReviewId(gkh.getKeyAs(Integer.class));
        return review;
    }

    @Override
    public void update(Review newReview) {
        jdbc.update(UPDATE_QUERY,
                new MapSqlParameterSource("content", newReview.getContent())
                        .addValue("is_positive", newReview.getIsPositive())
                        .addValue("review_id", newReview.getReviewId()));
    }

    @Override
    public void deleteById(int id) {
        deleteLikes(id);
        jdbc.update(DELETE_BY_ID_QUERY,
                new MapSqlParameterSource("review_id", id));
    }

    private void deleteLikes(int id) {
        jdbc.update(DELETE_LIKES_BY_REVIEW_QUERY,
                new MapSqlParameterSource("review_id", id));
    }

    private void refreshRating(int id) {
        jdbc.update(REFRESH_RATING_QYERY,
                new MapSqlParameterSource("review_id", id));
    }

    private void addLike(int reviewId, int userId, boolean isLike) {
        jdbc.update(ADD_LIKE_QUERY,
                new MapSqlParameterSource("review_id", reviewId)
                        .addValue("user_id", userId)
                        .addValue("is_like", isLike));
        refreshRating(reviewId);
    }

    private void deleteLike(int reviewId, int userId, boolean isLike) {
        jdbc.update(DELETE_LIKE_QUERY,
                new MapSqlParameterSource("review_id", reviewId)
                        .addValue("user_id", userId)
                        .addValue("is_like", isLike));
        refreshRating(reviewId);
    }

    @Override
    public void addLike(Review review, User user) {
        addLike(review.getReviewId(), user.getId(), true);
    }

    @Override
    public void addDislike(Review review, User user) {
        addLike(review.getReviewId(), user.getId(), false);
    }

    @Override
    public void deleteLike(Review review, User user) {
        deleteLike(review.getReviewId(), user.getId(), true);
    }

    @Override
    public void deleteDislike(Review review, User user) {
        deleteLike(review.getReviewId(), user.getId(), false);
    }

    @Override
    public void deleteFilmReviewLikes(int filmId) {
        jdbc.update(DELETE_REVIEW_LIKES_BY_FILM_REVIEW_QUERY,
                new MapSqlParameterSource("film_id", filmId));
    }

    @Override
    public void deleteFilmReviews(int filmId) {
        jdbc.update(DELETE_FILM_REVIEWS_QUERY,
                new MapSqlParameterSource("film_id", filmId));
    }

    @Override
    public void deleteUserReviewLikes(int userId) {
        jdbc.update(DELETE_REVIEW_LIKES_BY_USER_REVIEW_QUERY,
                new MapSqlParameterSource("user_id", userId));
    }

    @Override
    public void deleteUserReviews(int userId) {
        jdbc.update(DELETE_USER_REVIEWS_QUERY,
                new MapSqlParameterSource("user_id", userId));
    }

    @Override
    public List<Integer> findUserReviewLikes(int userId) {
        return jdbc.query(FIND_USER_REVIEW_LIKES_QUERY,
                new MapSqlParameterSource("user_id", userId),
                (ResultSet rs, int rowNum) -> rs.getInt("review_id"));
    }

    @Override
    public void deleteReviewLikesByUser(int userId) {
        jdbc.update(DELETE_REVIEW_LIKES_BY_USER_QUERY,
                new MapSqlParameterSource("user_id", userId));
    }

    @Override
    public void refreshRatings(List<Integer> ids) {
        MapSqlParameterSource[] batchArgs = ids.stream()
                .map(id -> new MapSqlParameterSource("review_id", id))
                .toArray(MapSqlParameterSource[]::new);
        jdbc.batchUpdate(REFRESH_RATING_QYERY, batchArgs);
    }
}
