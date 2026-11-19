<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Change Game Genre</title>
</head>
<body>
	<h1>Change Game Genre</h1>
	<p><span style="color:green"><b>${messages.success}</b></span></p>
	<p><span style="color:red"><b>${messages.error}</b></span></p>

	<c:if test="${not empty gameGenre}">
		<p>Game: <c:out value="${gameGenre.game.name}"/> (app_id=${gameGenre.game.appId})</p>
		<p>Current genre: <c:out value="${gameGenre.genre.name}"/></p>

		<form action="gamegenreupdate" method="post">
			<input type="hidden" name="appId" value="${gameGenre.game.appId}">
			<input type="hidden" name="genreId" value="${gameGenre.genre.genreId}">
			<label for="newGenreId">New genre</label>
			<select id="newGenreId" name="newGenreId">
				<c:forEach var="g" items="${allGenres}">
					<option value="${g.genreId}" ${g.genreId == gameGenre.genre.genreId ? 'selected' : ''}><c:out value="${g.name}"/></option>
				</c:forEach>
			</select>
			<input type="submit" value="Change">
		</form>
		<p><a href="gamegenres?appId=${gameGenre.game.appId}">Back to this game's genres</a></p>
	</c:if>
</body>
</html>
