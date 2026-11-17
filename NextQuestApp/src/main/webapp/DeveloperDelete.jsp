<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Delete Developer</title>
</head>
<body>
	<h1>Delete Developer</h1>
	<p><span style="color:green"><b>${messages.success}</b></span></p>
	<p><span style="color:red"><b>${messages.error}</b></span></p>

	<c:if test="${not empty developer}">
		<p>Are you sure you want to delete this developer?</p>
		<p>
			Developer ID: <c:out value="${developer.developerId}"/>
		</p>
		<p>
			Name: <c:out value="${developer.name}"/>
		</p>

		<form action="developerdelete" method="post">
			<input type="hidden" name="developerId" value="${developer.developerId}">
			<input type="submit" value="Delete Developer">
		</form>
	</c:if>

	<p><a href="developers">Back to Developers</a></p>
</body>
</html>
