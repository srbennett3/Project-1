<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Remove Game Genre</title>
</head>
<body>
	<h1>Remove Game Genre</h1>
	<p><span style="color:green"><b>${messages.success}</b></span></p>
	<p><span style="color:red"><b>${messages.error}</b></span></p>

	<c:if test="${not empty gameGenre}">
		<p>Remove genre <b><c:out value="${gameGenre.genre.name}"/></b> from
		   <b><c:out value="${gameGenre.game.name}"/></b>? The genre itself is kept.</p>
		<form action="gamegenredelete" method="post">
			<input type="hidden" name="appId" value="${gameGenre.game.appId}">
			<input type="hidden" name="genreId" value="${gameGenre.genre.genreId}">
			<input type="submit" value="Remove">
		</form>
		<p><a href="gamegenres?appId=${gameGenre.game.appId}">Back to this game's genres</a></p>
	</c:if>
	<c:if test="${not empty appId}">
		<p><a href="gamegenres?appId=${appId}">Back to this game's genres</a></p>
	</c:if>
</body>
</html>
