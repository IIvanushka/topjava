<%@ page import="ru.javawebinar.topjava.util.TimeUtil" %><%--
  Created by IntelliJ IDEA.
  User: Johnson
  Date: 06.11.2017
  Time: 13:02
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://java.sun.com/jsp/jstl/fmt" %>


<html>
<head>
    <title>Meals Table</title>

    <style type="text/css">

        .tg {
            border-collapse: collapse;
            border-spacing: 0;
            border-color: #ccc;
        }

        .tg .true {
            color: red;
            border-collapse: collapse;
            border-spacing: 0;
            font-family: Arial, sans-serif;
            font-size: 14px;
            padding: 10px 5px;
            border: 1px solid #ccc;
            word-break: normal;
            background-color: #fff;
        }

        .tg .false {
            color: black;
            border-collapse: collapse;
            border-spacing: 0;
            font-family: Arial, sans-serif;
            font-size: 14px;
            padding: 10px 5px;
            border: 1px solid #ccc;
            word-break: normal;
            background-color: #fff;
        }

        .tg th {
            font-family: Arial, sans-serif;
            font-size: 14px;
            font-weight: normal;
            padding: 10px 5px;
            word-break: normal;
            border: 1px solid #ccc;
            color: #333;
            background-color: #f0f0f0;
        }

        .tg td {
            padding: 10px 5px;
            text-align: center;
            border: 1px double #ccc;
        }
    </style>

</head>
<body>
<h2><a href="index.html">Home</a></h2>
<h2>MealsList</h2>
<table class="tg">
    <tr>
        <th width="150">Дата</th>
        <th width="150">Описание</th>
        <th width="150">Калории</th>
        <th width="150">Действие</th>
    </tr>

    <c:forEach items="${mealWithExceeds}" var="meal">
        <jsp:useBean id="meal" scope="page" class="ru.javawebinar.topjava.model.MealWithExceed"/>
        <tr class="${meal.exceed}">
            <%--<td width="150"><fmt:parseDate value="${meal.dateTime}" pattern="yyyy-MM-dd'T'HH:mm" var="time"--%>
                                           <%--type="both"/>--%>
                <%--<fmt:formatDate value="${time}" pattern="yyyy-MM-dd HH:mm"/></td>--%>
            <td><%=TimeUtil.toString(meal.getDateTime())%></td>
            <td width="150">${meal.description}</td>
            <td width="150">${meal.calories}</td>
            <td width="150"><a href="meals?action=delete&mealId=<c:out value="${meal.id}"/>">Удалить</a>
                <a href="meals?action=update&mealId=<c:out value="${meal.id}"/>">Обновить</a>
            </td>
        </tr>
    </c:forEach>
</table>
<form action="aumeals.jsp" method="get">
    <input type="submit" value="ADD NEW">
</form>
</body>
</html>
