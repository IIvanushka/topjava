package ru.javawebinar.topjava.web;

import org.slf4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

import ru.javawebinar.topjava.dao.DbMock;
import ru.javawebinar.topjava.model.MealWithExceed;


public class MealServlet extends HttpServlet {

    private static final Logger log = getLogger(MealServlet.class);

    private DbMock dbMock = DbMock.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String action = req.getParameter("action");
        if (action != null) {
            switch (action) {
                case "delete": {
                    dbMock.deleteMealbyId(Long.parseLong(req.getParameter("mealId")));
                    log.debug("Deleting meal with ID = "+Long.parseLong(req.getParameter("mealId")));
                    resp.sendRedirect("meals");
                }
            }
        } else {

            log.debug("Redirecting to Meals table.");

            List<MealWithExceed> mealWithExceeds = dbMock.getAllMealsWE();

            req.setAttribute("mealWithExceeds", mealWithExceeds);

            req.getRequestDispatcher("/meals.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.debug("Post");
    }
}
