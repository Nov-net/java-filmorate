package ru.yandex.practicum.filmorate.model;

import javax.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.Objects;
import java.util.TreeSet;

@Data
public class User {
    @NotEmpty
    @NotBlank
    String login;
    String name;
    Long id;
    @Email
    String email;
    @Past
    LocalDate birthday;

    TreeSet<Long> friends;

    public User(String login, String name, Long id, String email, LocalDate birthday) {
        this.id = id;
        this.name = name;
        this.login = login;
        this.email = email;
        this.birthday = birthday;
    }

    public boolean addAsFriend(Long friendId) {
        if (friends == null && id != friendId) {
            friends = new TreeSet<>();
            friends.add(friendId);
            return true;
        } else if (!friends.contains(friendId) && id != friendId){
            friends.add(friendId);
            return true;
        } else {
            return false;
        }
    }

    public boolean deleteFromFriend (Long friendId) {
        if (friends.contains(friendId)) {
            friends.remove(friendId);
            return true;
        } else {
            return false;
        }
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
