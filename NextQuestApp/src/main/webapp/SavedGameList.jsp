<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Saved Games</title>
</head>
<body>
	<h1>Saved Games</h1>
	<p><a href="index.jsp">Home</a> | <a href="users">Users</a></p>
	<p><span style="color:green"><b>${messages.success}</b></span></p>
	<p><span style="color:red"><b>${messages.error}</b></span></p>

	<form action="savedgames" method="get">
		<label for="userId">userId</label>
		<input id="userId" name="userId" value="${fn:escapeXml(param.userId)}">
		<input type="submit" value="Show Saved Games">
	</form>

	<c:if test="${not empty user}">
		<h2>Saved by <c:out value="${user.username}"/> (user_id=${user.userId})</h2>

		<h3>Save a Game</h3>
		<form action="savedgames" method="post">
			<input type="hidden" name="userId" value="${user.userId}">
			<label for="appId">appId</label>
			<input id="appId" name="appId" value="">
			<input type="submit" value="Save">
		</form>

		<h3>Saved Games</h3>
		<c:choose>
			<c:when test="${empty savedGames}">
				<p>No saved games yet.</p>
			</c:when>
			<c:otherwise>
				<table border="1">
					<tr>
						<th>app_id</th>
						<th>Game</th>
						<th>Price</th>
						<th>Saved at</th>
						<th></th>
					</tr>
					<c:forEach var="s" items="${savedGames}">
						<tr>
							<td><c:out value="${s.game.appId}"/></td>
							<td><a href="gamedetail?appId=${s.game.appId}"><c:out value="${s.game.name}"/></a></td>
							<td>$<c:out value="${s.game.price}"/></td>
							<td><fmt:formatDate value="${s.savedAt}" pattern="yyyy-MM-dd HH:mm"/></td>
							<td><a href="savedgamedelete?userId=${s.user.userId}&amp;appId=${s.game.appId}">Remove</a></td>
						</tr>
					</c:forEach>
				</table>
			</c:otherwise>
		</c:choose>
	</c:if>
</body>
</html>
