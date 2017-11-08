<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--
  Created by IntelliJ IDEA.
  User: Johnson
  Date: 08.11.2017
  Time: 10:20
  To change this template use File | Settings | File Templates.
--%>
<html>
<head>
    <title>Add/Update meals</title>
</head>
<body>

<h2><a href="index.html">Home</a></h2>
<h2><a href="meals">Back</a></h2>

<br>
<th2><b>ADD/Edit meal</b></th2>
<br>
<form method="post" action="meals" name="AddMeal">
    Meal ID : <input type="text" readonly name="id"
                     value="<c:out value="${meal.id}" />" /> <br />
    Прием пищи: <input
        type="text" name="description"
        value="<c:out value="${meal.description}" />"/> <br/>
    Каллории : <input
        type="text" name="calories"
        value="<c:out value="${meal.calories}" />"/> <br/>
    Дата : <input type="text" name="dateTime"
                  value="<fmt:parseDate value="${meal.dateTime}" pattern="yyyy-MM-dd'T'HH:mm" var="time"
                                           type="both"/>
                <fmt:formatDate value="${time}" pattern="yyyy-MM-dd HH:mm"/>"/> <br/>
    <input
            type="submit" value="ADD/UPDATE"/>
</form>
</body>
</html>