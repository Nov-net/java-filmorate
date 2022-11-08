package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@Primary
@Slf4j
public class MpaDbStorage implements MpaStorage{
    private final JdbcTemplate jdbcTemplate;

    public MpaDbStorage(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate=jdbcTemplate;
    }

    // получение списка всех mpa
    @Override
    public List<Mpa> findAll() {
        log.info("Получение списка mpa");
        String sql = "select * from mpa";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeMpa(rs));
    }

    // поиск mpa по id
    @Override
    public Mpa findMpaById(Long id) {
        String sql = "select * from mpa where id = ?";

        try{
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeMpa(rs), id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(String.format("Mpa с id %d не найден", id));
        }
    }

    // получение mpa по id фильма
    @Override
    public Mpa getMpa(Long id) {
        log.info("Получение mpa фильма с id {}", id);
        if (getMpaId(id) != null) {
            return findMpaById(getMpaId(id));
        } else {
            return null;
        }
    }

    // добавление mpa
    public void addMpa(Film film) {
        String sql = "INSERT INTO FILM_MPA (film_id, mpa_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, film.getId(), film.getMpa().getId());
        log.info("Фильму с id {} присвоен mpa с id {}", film.getId(), film.getMpa().getId());
    }

    public void updateMpa(Film film) {
        String qsql = "delete from FILM_MPA where film_id = ?";
        log.info("У фильма с id {} удален существующиq mpa", film.getId());
        jdbcTemplate.update(qsql, film.getId());

        String sql = "INSERT INTO FILM_MPA (film_id, mpa_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, film.getId(), film.getMpa().getId());
        log.info("У фильма с id {}  установлен новый mpa с id {}", film.getId(), film.getMpa().getId());
    }

    private Long getMpaId(Long id) {
        log.info("Получение id mpa по id фильма {}", id);
        String sql = "select mpa_id from FILM_MPA where film_id = ?";

        try{
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeMpaId(rs), id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    private Long makeMpaId(ResultSet rs) throws SQLException {
        Long l = rs.getLong("mpa_id");

        if (l == null) {
            return null;
        }
        return l;
    }

    private Mpa makeMpa(ResultSet rs) throws SQLException {
        Mpa mpa = Mpa.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .build();

        if (mpa == null) {
            return null;
        }
        return mpa;
    }
}
