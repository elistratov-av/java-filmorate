package ru.yandex.practicum.filmorate.service.impl;

import jakarta.validation.ValidationException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;

@Service
public class UserValidator {
    public void validate(User user, final boolean checkId) {
        if (checkId && user.getId() == null)
            throw new ValidationException("Id должен быть указан");
        if (StringUtils.containsWhitespace(user.getLogin()))
            throw new ValidationException("Логин не может содержать пробелы");
    }
}
