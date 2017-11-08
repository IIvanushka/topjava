package ru.javawebinar.topjava;

import ru.javawebinar.topjava.dao.DbMock;
import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.time.Month;

/**
 * @see <a href="http://topjava.herokuapp.com">Demo</a>
 * @see <a href="https://github.com/JavaOPs/topjava">Initial project</a>
 */
public class Main {
    private static DbMock dbMock = DbMock.getInstance();

    public static void main(String[] args) {
        System.out.println("Hello Topjava Enterprise!");
        dbMock.addMeal(new Meal(7L, LocalDateTime.of(2015, Month.MAY, 30, 10, 0), "Завтрак", 500));
    }
}
