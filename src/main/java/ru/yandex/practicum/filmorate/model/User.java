package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Data
@Builder
public class User {
    Long id;
    String login;
    String name;
    String email;
    LocalDate birthday;
    List<Long> friends;

    public User(Long id, String login, String name, String email, LocalDate birthday, List<Long> friends) {
        this.id = id;
        this.name = name;
        this.login = login;
        this.email = email;
        this.birthday = birthday;
        this.friends = friends;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return login.equals(user.login) && name.equals(user.name) && email.equals(user.email) && birthday.equals(user.birthday);
    }

    @Override
    public int hashCode() {
        return Objects.hash(login, name, email, birthday);
    }
}
