<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Update User</title>
</head>
<body>
	<h1>Update User</h1>
	<p><span style="color:green"><b>${messages.success}</b></span></p>
	<p><span style="color:red"><b>${messages.error}</b></span></p>

	<c:if test="${not empty user}">
		<p>
			Username: <c:out value="${user.username}"/>
		</p>

		<form action="userupdate" method="post">
			<input type="hidden" name="userId" value="${user.userId}">
			<p>
				<label for="email">Email</label>
				<input id="email" name="email" type="email"
					value="<c:out value='${user.email}'/>">
			</p>
			<p><input type="submit" value="Update"></p>
		</form>
	</c:if>

	<p><a href="users">Back to Users</a></p>
</body>
</html>
