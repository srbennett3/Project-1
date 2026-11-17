<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Delete Steam Recommendation</title>
</head>
<body>
	<h1>Delete Steam Recommendation</h1>
	<p><a href="steamrecommendations">Back to Steam Recommendations</a></p>
	<p><span style="color:green"><b>${messages.success}</b></span></p>
	<p><span style="color:red"><b>${messages.error}</b></span></p>

	<form action="steamrecommendationdelete" method="get">
		<label for="steamRecommendationId">steam_recommendation_id</label>
		<input id="steamRecommendationId" name="steamRecommendationId"
			value="${fn:escapeXml(param.steamRecommendationId)}">
		<input type="submit" value="Load">
	</form>

	<c:if test="${not empty recommendation}">
		<p>Delete recommendation id=<c:out value="${recommendation.steamRecommendationId}"/>
			for app_id=<c:out value="${recommendation.game.appId}"/>?</p>
		<form action="steamrecommendationdelete" method="post">
			<input type="hidden" name="steamRecommendationId"
				value="${recommendation.steamRecommendationId}">
			<input type="submit" value="Delete">
		</form>
	</c:if>
</body>
</html>
