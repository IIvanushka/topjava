package ru.javawebinar.topjava.web;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.service.MealService;

import java.time.LocalDateTime;
import java.time.Month;

import static ru.javawebinar.topjava.MealTestData.MEAL;
import static ru.javawebinar.topjava.MealTestData.assertMatch;

@ContextConfiguration({"classpath:spring/spring-app.xml", "classpath:spring/spring-db.xml"})
@RunWith(SpringRunner.class)
public class JdbcMealRepositoryImplTest {
    static {
        // Only for postgres driver logging
        // It uses java.util.logging and logged via jul-to-slf4j bridge
        SLF4JBridgeHandler.install();
    }
    @Autowired
    private MealService mealService;

    @Autowired
    private MealRepository mealRepository;

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void save() throws Exception {
        Meal meal = new Meal(LocalDateTime.of(2017, Month.MAY, 31, 10, 0),
                "Завтрак", 1000);
        Meal savedMeal = mealService.update(meal, 100001);
        Assert.assertEquals(meal, savedMeal);
    }

    @Test
    public void delete() throws Exception {
    }

    @Test
    public void get() throws Exception {
        Meal meal = mealService.get(100002, 100000);
        System.out.println(meal+"---------------------");
        assertMatch(meal, MEAL);

    }

    @Test
    public void getAll() throws Exception {
    }

    @Test
    public void getBetween() throws Exception {
    }
}