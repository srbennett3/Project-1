<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Remove Saved Game</title>
</head>
<body>
	<h1>Remove Saved Game</h1>
	<p><span style="color:green"><b>${messages.success}</b></span></p>
	<p><span style="color:red"><b>${messages.error}</b></span></p>

	<c:if test="${not empty savedGame}">
		<p>Remove this game from <c:out value="${savedGame.user.username}"/>'s saved games?</p>
		<p>Game: <c:out value="${savedGame.game.name}"/> (app_id=${savedGame.game.appId})</p>
		<p>Saved at: <fmt:formatDate value="${savedGame.savedAt}" pattern="yyyy-MM-dd HH:mm"/></p>

		<form action="savedgamedelete" method="post">
			<input type="hidden" name="userId" value="${savedGame.user.userId}">
			<input type="hidden" name="appId" value="${savedGame.game.appId}">
			<input type="submit" value="Remove">
		</form>
		<p><a href="savedgames?userId=${savedGame.user.userId}">Back to Saved Games</a></p>
	</c:if>
	<c:if test="${not empty deletedUserId}">
		<p><a href="savedgames?userId=${deletedUserId}">Back to Saved Games</a></p>
	</c:if>
	<p><a href="users">Back to Users</a></p>
</body>
</html>
