package ru.yandex.practicum.filmorate.dal;

import ru.yandex.practicum.filmorate.model.Feed;

import java.util.List;

public interface FeedRepository {
    Feed create(Feed feed);

    List<Feed> findFeedByUserId(Integer userId);
}
