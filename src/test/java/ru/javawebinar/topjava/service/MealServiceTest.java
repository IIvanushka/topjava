package ru.javawebinar.topjava.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.util.List;

import static ru.javawebinar.topjava.MealTestData.MEAL;
import static ru.javawebinar.topjava.MealTestData.assertMatch;
import static ru.javawebinar.topjava.UserTestData.ADMIN_ID;
import static ru.javawebinar.topjava.UserTestData.USER_ID;

@ContextConfiguration({
        "classpath:spring/spring-app.xml",
        "classpath:spring/spring-db.xml"
})
@RunWith(SpringRunner.class)
@Sql(scripts = "classpath:db/populateDB.sql", config = @SqlConfig(encoding = "UTF-8"))
public class MealServiceTest {

    @Autowired
    private MealService service;

    @Autowired
    private JdbcTemplate jdbcTemplate;

//    @Before
//    public void setUp() {
//        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!BEFORE HERE!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
//        JdbcTestUtils.executeSqlScript(jdbcTemplate, new FileSystemResource("src/main/resources/db/populateDB.sql"), false);
//    }

    @Test
    public void get() throws Exception {
        Meal meal = service.get(100002, 100000);
        assertMatch(meal, MEAL);
    }

    @Test(expected = NotFoundException.class)
    public void getWithWrongId() throws Exception {
        Meal meal = service.get(100002, ADMIN_ID);
        assertMatch(meal, MEAL);
    }

    @Test(expected = NullPointerException.class)
    public void delete() throws Exception {
        service.delete(100002, USER_ID);
        assertMatch(service.get(100002, USER_ID), MEAL);
    }

    @Test(expected = NotFoundException.class)
    public void deleteWithWrongId() throws Exception {
        service.delete(100002, ADMIN_ID);
    }

    @Test
    public void getAll() throws Exception {
        List<Meal> mealList = service.getAll(USER_ID);
        assertMatch(mealList.get(mealList.size() - 1), MEAL);
    }

    @SuppressWarnings("Duplicates")
    @Test
    public void update() throws Exception {
        Meal meal = MEAL;
        meal.setId(100002);
        meal.setCalories(10000);
        Meal updatedMeal = service.update(meal, USER_ID);
        assertMatch(updatedMeal, meal);
    }

    @SuppressWarnings("Duplicates")
    @Test(expected = NotFoundException.class)
    public void updateWithWrongId() throws Exception {
        Meal meal = MEAL;
        meal.setId(100002);
        meal.setCalories(10000);
        Meal updatedMeal = service.update(meal, ADMIN_ID);
        assertMatch(updatedMeal, meal);
    }

    @Test
    public void create() throws Exception {
        Meal meal = service.create(MEAL, USER_ID);
        assertMatch(meal, MEAL);
    }

}