package ru.javawebinar.topjava.repository.jdbc;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.to.MealWithUserId;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import static org.slf4j.LoggerFactory.getLogger;

@Repository
public class JdbcMealRepositoryImpl implements MealRepository {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final Logger log = getLogger(JdbcMealRepositoryImpl.class);

    private static final BeanPropertyRowMapper<Meal> ROW_MAPPER = BeanPropertyRowMapper.newInstance(Meal.class);

    private static final BeanPropertyRowMapper<MealWithUserId> ROW_MAPPER_WITH_USERID = BeanPropertyRowMapper.newInstance(MealWithUserId.class);

    private final JdbcTemplate jdbcTemplate;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final SimpleJdbcInsert insertMeal;

    @Autowired
    public JdbcMealRepositoryImpl(DataSource dataSource, JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.insertMeal = new SimpleJdbcInsert(dataSource)
                .withTableName("meals")
                .usingGeneratedKeyColumns("id");
    }

    @Override
    public Meal save(Meal meal, int userId) {
        MapSqlParameterSource source = new MapSqlParameterSource()
                .addValue("id", meal.getId())
                .addValue("userId", userId)
                .addValue("dateTime", meal.getDateTime().format(DATE_TIME_FORMATTER))
                .addValue("description", meal.getDescription())
                .addValue("calories", meal.getCalories());

        if (meal.isNew()) {
            Number key = insertMeal.executeAndReturnKey(source);
            meal.setId(key.intValue());
        } else {
            log.debug("Update!!! " + source.getValue("dateTime"));
            source.addValue("dateTimeStamp", Timestamp.valueOf(meal.getDateTime()));
            if (namedParameterJdbcTemplate.update(
                    "UPDATE meals SET datetime=:dateTimeStamp, description=:description,  calories=:calories " +
                            "WHERE id=:id AND userid=:userId", source) == 1) {
                log.debug("Update " + meal);
                return meal;
            } else {
                log.debug("Update FAILED " + meal);
                return null;
            }
        }
        log.debug("SAVE " + meal);
        return meal;
    }

    @Override
    public boolean delete(int id, int userId) {
        return jdbcTemplate.update("DELETE FROM meals WHERE id=? AND userid=?", id, userId) != 0;
    }

    @Override
    public Meal get(int id, int userId) {
        List<MealWithUserId> mealsWithUserId = jdbcTemplate.query("SELECT * FROM meals WHERE id=?", ROW_MAPPER_WITH_USERID, id);
        MealWithUserId mealWithUserId = DataAccessUtils.singleResult(mealsWithUserId);
        int mealUserId = mealWithUserId.getUserId();
        List<Meal> meals = mealsWithUserId.stream()
                .map(mealWithUI -> new Meal(mealWithUI.getId(), mealWithUI.getDateTime(), mealWithUI.getDescription(), mealWithUI.getCalories()))
                .collect(Collectors.toList());
        if (mealUserId == userId) {
            return DataAccessUtils.singleResult(meals);
        } else return null;
    }

    @Override
    public List<Meal> getAll(int userId) {
        return jdbcTemplate.query("SELECT * FROM meals WHERE userid=? ORDER BY datetime DESC", ROW_MAPPER, userId);
    }

    @Override
    public List<Meal> getBetween(LocalDateTime startDate, LocalDateTime endDate, int userId) {
        return jdbcTemplate.query("SELECT * FROM meals WHERE userid=? AND datetime>=? AND datetime<=? ORDER BY datetime DESC", ROW_MAPPER, userId, startDate, endDate);
    }
}
