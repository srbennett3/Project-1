<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Users</title>
</head>
<body>
	<h1>Users</h1>
	<p><a href="index.jsp">Home</a></p>
	<p><span style="color:green"><b>${messages.success}</b></span></p>
	<p><span style="color:red"><b>${messages.error}</b></span></p>

	<h2>Create User</h2>
	<form action="users" method="post">
		<p>
			<label for="username">Username</label>
			<input id="username" name="username" value="">
		</p>
		<p>
			<label for="email">Email</label>
			<input id="email" name="email" type="email" value="">
		</p>
		<p><input type="submit" value="Create"></p>
	</form>

	<h2>All Users</h2>
	<c:choose>
		<c:when test="${empty users}">
			<p>No users.</p>
		</c:when>
		<c:otherwise>
			<table border="1">
				<tr>
					<th>user_id</th>
					<th>username</th>
					<th>email</th>
					<th></th>
					<th></th>
					<th></th>
					<th></th>
					<th></th>
				</tr>
				<c:forEach var="u" items="${users}">
					<tr>
						<td><c:out value="${u.userId}"/></td>
						<td><c:out value="${u.username}"/></td>
						<td><c:out value="${u.email}"/></td>
						<td><a href="userupdate?userId=${u.userId}">Update</a></td>
						<td><a href="userdelete?userId=${u.userId}">Delete</a></td>
						<td><a href="savedgames?userId=${u.userId}">Saved games</a></td>
						<td><a href="reviews?filterUserId=${u.userId}">Reviews</a></td>
						<td><a href="surveys?filterUserId=${u.userId}">Surveys</a></td>
					</tr>
				</c:forEach>
			</table>
		</c:otherwise>
	</c:choose>
</body>
</html>
