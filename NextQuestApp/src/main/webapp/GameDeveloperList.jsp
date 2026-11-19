<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Game Developers</title>
</head>
<body>
	<h1>Game Developers</h1>
	<p><a href="index.jsp">Home</a> | <a href="developers">All Developers</a></p>
	<p><span style="color:green"><b>${messages.success}</b></span></p>
	<p><span style="color:red"><b>${messages.error}</b></span></p>

	<form action="gamedevelopers" method="get">
		<label for="appId">appId</label>
		<input id="appId" name="appId" value="${fn:escapeXml(param.appId)}">
		<input type="submit" value="Show Developers">
	</form>

	<c:if test="${not empty game}">
		<h2>Developers of <a href="gamedetail?appId=${game.appId}"><c:out value="${game.name}"/></a> (app_id=${game.appId})</h2>
		<c:choose>
			<c:when test="${empty gameDevelopers}">
				<p>This game has no developers.</p>
			</c:when>
			<c:otherwise>
				<table border="1">
					<tr><th>developer_id</th><th>Developer</th><th></th><th></th></tr>
					<c:forEach var="gd" items="${gameDevelopers}">
						<tr>
							<td><c:out value="${gd.developer.developerId}"/></td>
							<td><a href="developergames?developerId=${gd.developer.developerId}"><c:out value="${gd.developer.name}"/></a></td>
							<td><a href="gamedeveloperupdate?appId=${game.appId}&amp;developerId=${gd.developer.developerId}">Change</a></td>
							<td><a href="gamedeveloperdelete?appId=${game.appId}&amp;developerId=${gd.developer.developerId}">Remove</a></td>
						</tr>
					</c:forEach>
				</table>
			</c:otherwise>
		</c:choose>

		<h3>Add a Developer</h3>
		<form action="gamedevelopers" method="post">
			<input type="hidden" name="appId" value="${game.appId}">
			<label for="developerName">Developer name</label>
			<input id="developerName" name="developerName" value="">
			<input type="submit" value="Add">
			<small>(an existing developer with this exact name is reused; otherwise a new one is created)</small>
		</form>
	</c:if>
</body>
</html>
