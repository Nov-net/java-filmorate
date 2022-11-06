package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
@Primary
@Slf4j
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate=jdbcTemplate;
    }

    @Override
    public User create(User user) {
        Long idn = 1L;
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("SELECT * FROM USERS ORDER BY ID DESC LIMIT 1");
        if (userRows.next()) {
            idn = userRows.getLong("id");
            log.info("Последний установленный id: {}", idn);
            idn++;
        }

        user.setId(idn);
        log.info("Установлен id пользователя: {}", idn);
        String sql = "INSERT INTO USERS (id, login, name, email, birthday) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, user.getId(), user.getLogin(), user.getName(),
                user.getEmail(), Date.valueOf(user.getBirthday()));
        log.info("Добавлен новый пользователь: {}", user);

        return findUserById(user.getId());
    }

    @Override
    public User update(User user) {
        String sql = "UPDATE USERS SET login = ?, name = ?, email = ?, birthday = ? WHERE id = ?";

        jdbcTemplate.update(sql
                , user.getLogin()
                , user.getName()
                , user.getEmail()
                , user.getBirthday()
                , user.getId());
        log.info("Пользователь обновлен: {}", user);

        return findUserById(user.getId());
    }

    @Override
    public List<User> findAll() {
        log.info("Получение списка пользователей");
        String sql = "select * from users";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs));
    }

    @Override
    public User findUserById(Long id) {
        String sql = "select * from users where id = ?";

        try{
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeUser(rs), id);
        } catch (EmptyResultDataAccessException e) {
            throw new UserNotFoundException(String.format("Пользователь с id %d не найден", id));
        }
    }

    @Override
    public String addAsFriend(Long id, Long friendId) {
        String sql = "INSERT INTO FRIENDS (user_id, friend_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, id, friendId);

        return String.format("Пользователь с id %d  добавлен в друзья к пользователю %d", friendId, id);
    }

    @Override
    public boolean deleteFromFriend(Long id, Long friendId) {
        log.info("Проверка наличия друга с id {} у пользователя c id {}", id, friendId);
        if (getIdFriends(id).contains(friendId)) {
            String sql = "delete from FRIENDS where user_id = ? and friend_id = ?";
            log.info("У пользователя с id {} удален из друзей пользователь с id {}", id, friendId);
            return jdbcTemplate.update(sql, id, friendId) > 0;
        } else {
            log.info("У пользователя с id {} нет друга с id {}", id, friendId);
            return false;
        }
    }

    @Override
    public List<User> mutualFriendsList(Long id, Long otherId) {
        List <User> list = new ArrayList<>();

        if(getIdFriends(id) == null || getIdFriends(otherId) == null) {
            return list;
        }

        for (Long i : getIdFriends(id)) {
            for (Long j : getIdFriends(otherId)) {
                if (i == j) {
                    list.add(findUserById(j));
                }
            }
        }

        return list;
    }

    @Override
    public List<User> getFriends(Long id) {
        List<User> list = new ArrayList<>();

        for(Long l : getIdFriends(id)) {
            list.add(findUserById(l));
        }
        return list;
    }

    private List<Long> getIdFriends(Long id) {
        log.info("Получение списка id друзей пользователя {}", id);
        String sql = "select friend_id from friends where user_id = ?";

        return jdbcTemplate.query(sql, (rs, rowNum) -> makeId(rs), id);
    }

    private User makeUser(ResultSet rs) throws SQLException {
        User user = User.builder()
                .id(rs.getLong("id"))
                .login(rs.getString("login"))
                .name(rs.getString("name"))
                .email(rs.getString("email"))
                .birthday(rs.getDate("birthday").toLocalDate())
                .friends(getIdFriends(rs.getLong("id")))
                .build();

        if (user == null) {
            return null;
        }
        return user;
    }

    private Long makeId (ResultSet rs) throws SQLException {
        Long l = rs.getLong("friend_id");

        if (l == null) {
            return null;
        }
        return l;
    }
}
