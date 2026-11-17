<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Update Steam Recommendation</title>
</head>
<body>
	<h1>Update Steam Recommendation</h1>
	<p><a href="steamrecommendations">Back to Steam Recommendations</a></p>
	<p><span style="color:green"><b>${messages.success}</b></span></p>
	<p><span style="color:red"><b>${messages.error}</b></span></p>

	<form action="steamrecommendationupdate" method="get">
		<label for="steamRecommendationId">steam_recommendation_id</label>
		<input id="steamRecommendationId" name="steamRecommendationId"
			value="${fn:escapeXml(param.steamRecommendationId)}">
		<input type="submit" value="Load">
	</form>

	<c:if test="${not empty recommendation}">
		<fmt:formatDate value="${recommendation.recommendationDate}"
			pattern="yyyy-MM-dd" var="recommendationDateText"/>
		<form action="steamrecommendationupdate" method="post">
			<input type="hidden" name="steamRecommendationId"
				value="${recommendation.steamRecommendationId}">
			<p><label for="appId">Game app_id</label>
				<input id="appId" name="appId" type="number" required
					value="${recommendation.game.appId}"></p>
			<p><label for="authorId">Steam author_id</label>
				<input id="authorId" name="authorId" type="number"
					value="${recommendation.authorId}"></p>
			<p><label for="isRecommended">Recommended</label>
				<select id="isRecommended" name="isRecommended">
					<c:choose>
						<c:when test="${empty recommendation.isRecommended}">
							<option value="" selected>Unknown</option>
						</c:when>
						<c:when test="${recommendation.isRecommended == true}">
							<option value="true" selected>Yes</option>
							<option value="false">No</option>
						</c:when>
						<c:otherwise>
							<option value="true">Yes</option>
							<option value="false" selected>No</option>
						</c:otherwise>
					</c:choose>
				</select></p>
			<p><label for="hoursPlayed">Hours played</label>
				<input id="hoursPlayed" name="hoursPlayed" type="number" step="0.01"
					value="${recommendation.hoursPlayed}"></p>
			<p><label for="recommendationDate">Recommendation date</label>
				<input id="recommendationDate" name="recommendationDate" type="date"
					value="${recommendationDateText}"></p>
			<p><input type="submit" value="Update"></p>
		</form>
	</c:if>
</body>
</html>
