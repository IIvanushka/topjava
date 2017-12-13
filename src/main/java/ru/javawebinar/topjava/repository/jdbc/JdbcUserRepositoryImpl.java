package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.UserRepository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class JdbcUserRepositoryImpl implements UserRepository {

    private static final BeanPropertyRowMapper<User> ROW_MAPPER = BeanPropertyRowMapper.newInstance(User.class);

    private final JdbcTemplate jdbcTemplate;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final SimpleJdbcInsert insertUser;

    private DataSourceTransactionManager transactionManager;

    @Autowired
    public JdbcUserRepositoryImpl(DataSourceTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
        this.insertUser = new SimpleJdbcInsert(transactionManager.getDataSource())
                .withTableName("users")
                .usingGeneratedKeyColumns("id");

        this.jdbcTemplate = new JdbcTemplate(transactionManager.getDataSource());
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(transactionManager.getDataSource());
    }

    @Override
    public User save(User user) {
        BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(user);
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("Saving / Updating");
        TransactionStatus status = transactionManager.getTransaction(def);

        try {
            if (user.isNew()) {
                Number newKey = insertUser.executeAndReturnKey(parameterSource);
                user.setId(newKey.intValue());
                user.getRoles().forEach((role) -> jdbcTemplate.update("INSERT INTO user_roles (user_id, role) VALUES (?, ?)", user.getId(), role.toString()));
            } else {
                namedParameterJdbcTemplate.update(
                        "UPDATE users SET name=:name, email=:email, password=:password, " +
                                "registered=:registered, enabled=:enabled, calories_per_day=:caloriesPerDay WHERE id=:id",
                        parameterSource);
            }
            transactionManager.commit(status);
        } catch (DataAccessException e) {
            transactionManager.rollback(status);
            throw new DataAccessException(e.toString(), e.getCause()) {};
        }
        return user;
    }

    @Override
    public boolean delete(int id) {
        return jdbcTemplate.update("DELETE FROM users WHERE id=?", id) != 0;
    }

    @Override
    public User get(int id) {
        List<User> users = jdbcTemplate.query("SELECT * FROM users WHERE id=?", ROW_MAPPER, id);
        return DataAccessUtils.singleResult(users);
    }

    @Override
    public User getByEmail(String email) {
//        return jdbcTemplate.queryForObject("SELECT * FROM users WHERE email=?", ROW_MAPPER, email);
        List<User> users = jdbcTemplate.query("SELECT * FROM users WHERE email=?", ROW_MAPPER, email);
        return DataAccessUtils.singleResult(users);
    }

    @Override
    public List<User> getAll() {
        return jdbcTemplate.query("SELECT * FROM users, user_roles WHERE users.id = user_roles.user_id ORDER BY name, email",
                rs -> {
                    List<User> users = new ArrayList<>();
                    List<Role> roles = new ArrayList<>();
                    Integer UserId = null;
                    User currentUser = null;
                    int userIdx = 0;
                    while (rs.next()) {
                        if (currentUser == null || !UserId.equals(rs.getInt("user_id"))) {
                            UserId = rs.getInt("user_id");
                            currentUser = ROW_MAPPER.mapRow(rs, userIdx++);
                            users.add(currentUser);
                            roles.clear();
                        }
                        roles.add(Role.valueOf(rs.getString(rs.findColumn("role"))));
                        currentUser.setRoles(roles);
                    }
                    return users;
                });
    }
}
