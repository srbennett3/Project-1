<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Remove Game Developer</title>
</head>
<body>
	<h1>Remove Game Developer</h1>
	<p><span style="color:green"><b>${messages.success}</b></span></p>
	<p><span style="color:red"><b>${messages.error}</b></span></p>

	<c:if test="${not empty gameDeveloper}">
		<p>Remove developer <b><c:out value="${gameDeveloper.developer.name}"/></b> from
		   <b><c:out value="${gameDeveloper.game.name}"/></b>? The developer itself is kept.</p>
		<form action="gamedeveloperdelete" method="post">
			<input type="hidden" name="appId" value="${gameDeveloper.game.appId}">
			<input type="hidden" name="developerId" value="${gameDeveloper.developer.developerId}">
			<input type="submit" value="Remove">
		</form>
		<p><a href="gamedevelopers?appId=${gameDeveloper.game.appId}">Back to this game's developers</a></p>
	</c:if>
	<c:if test="${not empty appId}">
		<p><a href="gamedevelopers?appId=${appId}">Back to this game's developers</a></p>
	</c:if>
</body>
</html>
