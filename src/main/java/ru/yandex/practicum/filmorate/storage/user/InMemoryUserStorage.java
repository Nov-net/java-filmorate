package ru.yandex.practicum.filmorate.storage.user;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();
    private final List <Long> listUsersId = new ArrayList<>();
    private long userId = 0;

    @Override
    public User create(User user) {
        userId++;
        user.setId(userId);
        listUsersId.add(userId);
        log.info("Установлен id пользователя: {}", userId);
        users.put(user.getId(), user);
        log.info("Добавлен новый пользователь: {}", user);
        return user;
    }

    @Override
    public User update(User user) {
        users.put(user.getId(), user);
        log.info("Пользователь обновлен: {}", user);
        return user;
    }

    @Override
    public List<User> findAll() {
        log.info("Текущее количество пользователей: {}", users.size());
        List <User> list = new ArrayList<>();
        for(User u : users.values()) {
            list.add(u);
        }
        return list;
    }

    @Override
    public ArrayList<Long> getListUsersId() {
        return new ArrayList<>(listUsersId);
    }

    // получение списка пользователей для теста (Спринт 9)
    public Map<Long, User> findAllForTest() {
        return users;
    }
}
