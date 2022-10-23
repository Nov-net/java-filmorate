package ru.yandex.practicum.filmorate.service;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class UserService {
    private UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @SneakyThrows
    public User create(User user) {
        if(userStorage.getListUsersId().contains(user.getId())) {
            log.info("Попытка добавить пользователя с уже существующим id");
            throw new UserAlreadyExistException(String.format("id %d уже существует", user.getId()));
        }
        if(userStorage.findAll().contains(user)) {
            log.info("Попытка добавить уже существующего пользователя");
            throw new UserAlreadyExistException("Пользователь уже существует");
        }
        if(user.getLogin() == null || user.getLogin().isEmpty() || user.getLogin().isBlank()) {
            log.info("Попытка создать пользователя без указания логина");
            throw new InvalidNameException("Отсутствует логин пользователя");
        }
        if(user.getEmail() == null || !user.getEmail().contains("@")) {
            log.info("Попытка создать пользователя с некорректным email");
            throw new InvalidEmailException(String.format("Некорректный email: %s", user.getEmail()));
        }
        if(user.getBirthday().isAfter(LocalDate.now())) {
            log.info("Попытка добавить пользователя из будущего");
            throw new InvalidBirthdateException(String.format("Некорректная дата рождения пользователя: %s", user.getBirthday()));
        }
        if(user.getName() == null || user.getName().isEmpty() || user.getName().isBlank()) {
            log.info("Попытка добавить пользователя с пустым полем name");
            user.setName(user.getLogin());
            log.info("В значение пустого поля name установлен логин пользователя: {}", user.getLogin());
        }

        return userStorage.create(user);
    }

    @SneakyThrows
    public User update(User user) {
        if(user.getId() == null || !userStorage.getListUsersId().contains(user.getId())) {
            log.info("Попытка обновить пользователя с пустым или несуществующим id");
            throw new InvalidIdException(String.format("Пользователь с пустым или несуществующим id %d", user.getId()));
        }
        if(user.getLogin() == null || user.getLogin().isEmpty() || user.getLogin().isBlank()) {
            log.info("Попытка обновить пользователя без указания логина");
            throw new InvalidNameException("Отсутствует логин пользователя");
        }
        if(user.getEmail() == null || !user.getEmail().contains("@")) {
            log.info("Попытка обновить пользователя с некорректным email");
            throw new InvalidEmailException(String.format("Некорректный email: %s", user.getEmail()));
        }
        if(user.getBirthday().isAfter(LocalDate.now())) {
            log.info("Попытка обновить пользователя с датой рождения в будущем");
            throw new InvalidBirthdateException(String.format("Некорректная дата рождения пользователя: %s", user.getBirthday()));
        }
        if(user.getName() == null || user.getName().isEmpty() || user.getName().isBlank()) {
            log.info("Попытка обновить пользователя с пустым полем name");
            user.setName(user.getLogin());
            log.info("В значение пустого поля name установлен логин пользователя: {}", user.getLogin());
        }

        return userStorage.update(user);
    }

    public List<User> findAll() {
        return userStorage.findAll();
    }

    // поиск пользователя по id
    public User findUserById(Long id) {
        return userStorage.findAll().stream()
                          .filter(p -> p.getId().equals(id))
                          .findFirst()
                          .orElseThrow(() -> new UserNotFoundException(String.format("Пользователь с id %d не найден", id)));
    }

    // получение писка друзей
    public List<User> getFriends(Long id) {
        if (id == null) {
            log.info("Попытка получить список друзей пользователя с пустым id");
            throw new InvalidIdException(String.format("Пользователь с пустым id"));
        }
        if(!userStorage.getListUsersId().contains(id)) {
            log.info("Попытка получить список друзей пользователя с несуществующим id");
            throw new InvalidIdException(String.format("Пользователь с несуществующим id %d", id));
        }

        List<User> list = new ArrayList<>();
        for(Long l : findUserById(id).getFriends()) {
            list.add(findUserById(l));
        }

        return list;
    }

    // добавление в друзья
    public String addAsFriend(Long id, Long friendId) {
        checkId(id, friendId);
        if(userStorage.getListUsersId().contains(id)) {
            if (userStorage.getListUsersId().contains(friendId)) {
                if (id != friendId) {
                    findUserById(id).addAsFriend(friendId);
                    findUserById(friendId).addAsFriend(id);
                    log.info("Пользователи с id {} и {} добавлены в друзья", id, friendId);
                    return String.format("Пользователи с id %d и %d добавлены в друзья", id, friendId);
                } else {
                    log.info("Попытка добавить пользователя с id {} в друзья к пользователю с id  {}", id, friendId);
                    throw new InvalidIdException ("Вы не можете добавить себя к себе в друзья");
                }
            } else {
                log.info("Попытка добавить в друзья пользователя с несуществующим id {}", friendId);
                throw new UserNotFoundException(String.format("Пользователь с id %d не найден", friendId));
            }

        } else {
            log.info("Попытка добавить друга к пользователю с несуществующим id {}", id);
            throw new UserNotFoundException(String.format("Пользователь с id %d не найден", id));
        }
    }

    // удаление из друзей
    public String deleteFromFriend(Long id, Long friendId) {
        checkId(id, friendId);
        if(userStorage.getListUsersId().contains(id)) {
            if (userStorage.getListUsersId().contains(friendId)) {
                findUserById(id).deleteFromFriend(friendId);
                log.info("У пользователя с id {} удален из друзей пользователь с id {}", id, friendId);
                findUserById(friendId).deleteFromFriend(id);
                log.info("У пользователя с id {} удален из друзей пользователь с id {}", friendId, id);
                log.info("Пользователи с id {} и {} удалены из друзей", id, friendId);
                return String.format("Пользователи с id %d и %d удалены из друзей", id, friendId);
            } else {
                log.info("Попытка удалить из друзей пользователя с несуществующим id {}", friendId);
                throw new UserNotFoundException(String.format("Пользователь с id %d не найден", friendId));
            }

        } else {
            log.info("Попытка удалить друга у пользователя с несуществующим id {}", id);
            throw new UserNotFoundException(String.format("Пользователь с id %d не найден", id));
        }
    }

    // список общих друзей
    public List<User> mutualFriendsList(Long id, Long otherId) {
        List <User> list = new ArrayList<>();

        checkId(id, otherId);
        if(findUserById(id).getFriends() == null || findUserById(otherId).getFriends() == null) {
            return list;
        } else {
            for (Long i : findUserById(id).getFriends()) {
                for (Long j : findUserById(otherId).getFriends()) {
                    if (i == j) {
                        list.add(findUserById(j));
                    }
                }
            }
        }
        return list;
    }

    private void checkId(Long id, Long friendId) {
        if (id == null || id < 1 || friendId == null || friendId < 1) {
            log.info("Пользователь с пустым или отрицательным id {}");
            throw new InvalidIdException("Пользователь с пустым или отрицательным id");
        }
    }

}
