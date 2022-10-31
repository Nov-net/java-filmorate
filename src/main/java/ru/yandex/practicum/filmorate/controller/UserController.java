package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    UserService userService;
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.info("Получен запрос POST/users - создание нового пользователя");
        return userService.create(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        log.info("Получен запрос PUT/users - обновление пользователя с id {}", user.getId());
        return userService.update(user);
    }

    @GetMapping
    public List<User> findAll() {
        log.info("Получен запрос GET/users - получение списка пользователей");
        return userService.findAll();
    }

    // GET /users/{id} — поиск пользователя по id
    @GetMapping("/{id}")
    public User findUserById(@PathVariable(required = false) Long id) {
        log.info("Получен запрос GET/users/{id} - получение пользователя по id");
        return userService.findUserById(id);
    }

    // PUT /users/{id}/friends/{friendId} — добавление в друзья
    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable("id") Long id) {
        log.info("Получен запрос GET/users/{id}/friends - получение списка друзей");
        return userService.getFriends(id);
    }

    // PUT /users/{id}/friends/{friendId} — добавление в друзья
    @PutMapping("/{id}/friends/{friendId}")
    public String addAsFriend(@PathVariable("id") Long id, @PathVariable("friendId") Long friendId) {
        log.info("Получен запрос PUT/users/{id}/friends/{friendId} - добавление в друзья");
        return userService.addAsFriend(id, friendId);
    }

    // DELETE /users/{id}/friends/{friendId} — удаление из друзей
    @DeleteMapping("/{id}/friends/{friendId}")
    public String deleteFromFriend(@PathVariable("id") Long id, @PathVariable("friendId") Long friendId) {
        log.info("Получен запрос DELETE/users/{id}/friends/{friendId} — удаление из друзей");
        return userService.deleteFromFriend(id, friendId);
    }

    // GET /users/{id}/friends/common/{otherId} — список друзей, общих с другим пользователем
    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> mutualFriendsList(@PathVariable("id") Long id, @PathVariable("otherId") Long otherId) {
        log.info("Получен запрос GET/users/{id}/friends/common/{otherId} — список общих друзей");
        return userService.mutualFriendsList(id, otherId);
    }

    // получение списка пользователей для теста (Спринт 9)
    public Map<Long, User> findAllForTest() {
        InMemoryUserStorage inMemoryUserStorage = new InMemoryUserStorage();
        return inMemoryUserStorage.findAllForTest();
    }

}
