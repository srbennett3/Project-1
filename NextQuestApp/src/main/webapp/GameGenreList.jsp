<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Game Genres</title>
</head>
<body>
	<h1>Game Genres</h1>
	<p><a href="index.jsp">Home</a> | <a href="genres">All Genres</a></p>
	<p><span style="color:green"><b>${messages.success}</b></span></p>
	<p><span style="color:red"><b>${messages.error}</b></span></p>

	<form action="gamegenres" method="get">
		<label for="appId">appId</label>
		<input id="appId" name="appId" value="${fn:escapeXml(param.appId)}">
		<input type="submit" value="Show Genres">
	</form>

	<c:if test="${not empty game}">
		<h2>Genres of <a href="gamedetail?appId=${game.appId}"><c:out value="${game.name}"/></a> (app_id=${game.appId})</h2>
		<c:choose>
			<c:when test="${empty gameGenres}">
				<p>This game has no genres.</p>
			</c:when>
			<c:otherwise>
				<table border="1">
					<tr><th>genre_id</th><th>Genre</th><th></th><th></th></tr>
					<c:forEach var="gg" items="${gameGenres}">
						<tr>
							<td><c:out value="${gg.genre.genreId}"/></td>
							<td><a href="genregames?genreId=${gg.genre.genreId}"><c:out value="${gg.genre.name}"/></a></td>
							<td><a href="gamegenreupdate?appId=${game.appId}&amp;genreId=${gg.genre.genreId}">Change</a></td>
							<td><a href="gamegenredelete?appId=${game.appId}&amp;genreId=${gg.genre.genreId}">Remove</a></td>
						</tr>
					</c:forEach>
				</table>
			</c:otherwise>
		</c:choose>

		<h3>Add a Genre</h3>
		<form action="gamegenres" method="post">
			<input type="hidden" name="appId" value="${game.appId}">
			<select name="genreId">
				<option value="">-- choose --</option>
				<c:forEach var="g" items="${allGenres}">
					<option value="${g.genreId}"><c:out value="${g.name}"/></option>
				</c:forEach>
			</select>
			<input type="submit" value="Add">
		</form>
	</c:if>
</body>
</html>
