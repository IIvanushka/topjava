package ru.javawebinar.topjava.web;

import org.slf4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.slf4j.LoggerFactory.getLogger;

import ru.javawebinar.topjava.dao.DbMock;
import ru.javawebinar.topjava.model.Meal;


public class MealServlet extends HttpServlet {

    private static final Logger log = getLogger(MealServlet.class);

    private DbMock dbMock = DbMock.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String action = req.getParameter("action");
        if (action != null) {
            switch (action) {
                case "delete": {
                    dbMock.deleteMealById(Long.parseLong(req.getParameter("mealId")));
                    log.debug("Deleting meal with ID = " + Long.parseLong(req.getParameter("mealId")));
                    resp.sendRedirect("meals");
                    break;
                }
                case "update": {
                    Meal meal = dbMock.getMealById(Long.parseLong(req.getParameter("mealId")));
                    req.setAttribute("meal", meal);
                    log.debug("Open update form for " + meal);

                    req.getRequestDispatcher("/aumeals.jsp").forward(req, resp);
                    break;
                }
            }
        } else {

            log.debug("Redirecting to Meals table.");

            req.setAttribute("mealWithExceeds", dbMock.getAllMealsWE());

            req.getRequestDispatcher("/meals.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html; charset=UTF-8");
        req.setCharacterEncoding("UTF-8");
        String id = req.getParameter("id");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        if (id == null || id.isEmpty()) {
            Meal meal = new Meal(dbMock.getNextId(),
                    LocalDateTime.parse(req.getParameter("dateTime").trim(), formatter), req.getParameter("description"),
                    Integer.parseInt(req.getParameter("calories")));
            dbMock.addMeal(meal);
            log.debug("Add meal " + meal);
        } else {
            Meal meal = new Meal(Long.parseLong(id),
                    LocalDateTime.parse(req.getParameter("dateTime").trim(), formatter), req.getParameter("description"),
                    Integer.parseInt(req.getParameter("calories")));
            dbMock.addMeal(meal);
            log.debug("Update meal " + meal);
        }
        req.setAttribute("mealWithExceeds", dbMock.getAllMealsWE());

        req.getRequestDispatcher("/meals.jsp").forward(req, resp);
    }
}
