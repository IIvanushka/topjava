package ru.javawebinar.topjava.web;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.jdbc.JdbcMealRepositoryImpl;

import java.time.LocalDateTime;
import java.time.Month;

@ContextConfiguration({"classpath:spring/spring-app.xml", "classpath:spring/spring-db.xml"})
@RunWith(SpringRunner.class)
public class JdbcMealRepositoryImplTest {

    @Autowired
    private JdbcMealRepositoryImpl jdbcMealRepository;

    @Test
    public void save() throws Exception {
        Meal meal = new Meal(LocalDateTime.of(2017, Month.MAY, 31, 10, 0),
                "Завтрак", 1000);
        Meal savedMeal = jdbcMealRepository.save(meal, 100001);
        Assert.assertEquals(meal, savedMeal);
    }

}