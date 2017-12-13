package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class JdbcMealRepositoryImpl implements MealRepository {

    private static final RowMapper<Meal> ROW_MAPPER = BeanPropertyRowMapper.newInstance(Meal.class);

    private final JdbcTemplate jdbcTemplate;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final SimpleJdbcInsert insertMeal;

    private DataSourceTransactionManager transactionManager;

    @Autowired
    public JdbcMealRepositoryImpl(DataSourceTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
        this.insertMeal = new SimpleJdbcInsert(transactionManager.getDataSource())
                .withTableName("meals")
                .usingGeneratedKeyColumns("id");

        this.jdbcTemplate = new JdbcTemplate(transactionManager.getDataSource());
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(transactionManager.getDataSource());
    }

    @Override
    public Meal save(Meal meal, int userId) {

        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("Saving / Updating");
        TransactionStatus status = transactionManager.getTransaction(def);

        MapSqlParameterSource map = new MapSqlParameterSource()
                .addValue("id", meal.getId())
                .addValue("description", meal.getDescription())
                .addValue("calories", meal.getCalories())
                .addValue("date_time", meal.getDateTime())
                .addValue("user_id", userId);

        if (meal.isNew()) {

            Number newId;
            try {
                newId = insertMeal.executeAndReturnKey(map);
                meal.setId(newId.intValue());
            } catch (DataAccessException e) {
                transactionManager.rollback(status);
            }

        } else {
            try {
                boolean res = namedParameterJdbcTemplate.update("" +
                                "UPDATE meals " +
                                "   SET description=:description, calories=:calories, date_time=:date_time " +
                                " WHERE id=:id AND user_id=:user_id"
                        , map) == 1;
                if (res) {
                    transactionManager.commit(status);
                } else {
                    return null;
                }
            } catch (DataAccessException e) {
                transactionManager.rollback(status);
                return null;
            }
        }
        return meal;
    }

    @Override
    public boolean delete(int id, int userId) {

        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("Deleting.");
        TransactionStatus status = transactionManager.getTransaction(def);
        boolean result = false;
        try {
            result = jdbcTemplate.update("DELETE FROM meals WHERE id=? AND user_id=?", id, userId) != 0;
            transactionManager.commit(status);
        } catch (DataAccessException e) {
            result = false;
            transactionManager.rollback(status);
        }
        return result;
    }

    @Override
    public Meal get(int id, int userId) {
        List<Meal> meals = jdbcTemplate.query(
                "SELECT * FROM meals WHERE id = ? AND user_id = ?", ROW_MAPPER, id, userId);
        return DataAccessUtils.singleResult(meals);
    }

    @Override
    public List<Meal> getAll(int userId) {
        return jdbcTemplate.query(
                "SELECT * FROM meals WHERE user_id=? ORDER BY date_time DESC", ROW_MAPPER, userId);
    }

    @Override
    public List<Meal> getBetween(LocalDateTime startDate, LocalDateTime endDate, int userId) {
        return jdbcTemplate.query(
                "SELECT * FROM meals WHERE user_id=?  AND date_time BETWEEN  ? AND ? ORDER BY date_time DESC",
                ROW_MAPPER, userId, startDate, endDate);
    }
}
