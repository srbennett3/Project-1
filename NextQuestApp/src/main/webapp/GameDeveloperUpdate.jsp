<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Change Game Developer</title>
</head>
<body>
	<h1>Change Game Developer</h1>
	<p><span style="color:green"><b>${messages.success}</b></span></p>
	<p><span style="color:red"><b>${messages.error}</b></span></p>

	<c:if test="${not empty gameDeveloper}">
		<p>Game: <c:out value="${gameDeveloper.game.name}"/> (app_id=${gameDeveloper.game.appId})</p>
		<p>Current developer: <c:out value="${gameDeveloper.developer.name}"/></p>

		<form action="gamedeveloperupdate" method="post">
			<input type="hidden" name="appId" value="${gameDeveloper.game.appId}">
			<input type="hidden" name="developerId" value="${gameDeveloper.developer.developerId}">
			<label for="newDeveloperName">New developer name</label>
			<input id="newDeveloperName" name="newDeveloperName"
				value="<c:out value='${gameDeveloper.developer.name}'/>">
			<input type="submit" value="Change">
		</form>
		<p><a href="gamedevelopers?appId=${gameDeveloper.game.appId}">Back to this game's developers</a></p>
	</c:if>
</body>
</html>
