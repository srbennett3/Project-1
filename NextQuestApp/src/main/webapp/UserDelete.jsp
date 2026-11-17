<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Delete User</title>
</head>
<body>
	<h1>Delete User</h1>
	<p><span style="color:green"><b>${messages.success}</b></span></p>
	<p><span style="color:red"><b>${messages.error}</b></span></p>

	<c:if test="${not empty user}">
		<p>Are you sure you want to delete this user?</p>
		<p>
			User ID: <c:out value="${user.userId}"/>
		</p>
		<p>
			Username: <c:out value="${user.username}"/>
		</p>
		<p>
			Email: <c:out value="${user.email}"/>
		</p>

		<form action="userdelete" method="post">
			<input type="hidden" name="userId" value="${user.userId}">
			<input type="submit" value="Delete User">
		</form>
	</c:if>

	<p><a href="users">Back to Users</a></p>
</body>
</html>
