<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Update Steam Review</title>
</head>
<body>
	<h1>Update Steam Review</h1>
	<p><a href="steamreviews">Back to Steam Reviews</a></p>
	<p><span style="color:green"><b>${messages.success}</b></span></p>
	<p><span style="color:red"><b>${messages.error}</b></span></p>

	<form action="steamreviewupdate" method="get">
		<label for="steamReviewId">steam_review_id</label>
		<input id="steamReviewId" name="steamReviewId"
			value="${fn:escapeXml(param.steamReviewId)}">
		<input type="submit" value="Load">
	</form>

	<c:if test="${not empty review}">
		<fmt:formatDate value="${review.reviewDate}" pattern="yyyy-MM-dd"
			var="reviewDateText"/>
		<form action="steamreviewupdate" method="post">
			<input type="hidden" name="steamReviewId" value="${review.steamReviewId}">
			<p><label for="appId">Game app_id</label>
				<input id="appId" name="appId" type="number" required
					value="${review.game.appId}"></p>
			<p><label for="authorSteamId">Author Steam ID</label>
				<input id="authorSteamId" name="authorSteamId" type="number"
					value="${review.authorSteamId}"></p>
			<p><label for="isRecommended">Recommended</label>
				<select id="isRecommended" name="isRecommended">
					<c:choose>
						<c:when test="${empty review.isRecommended}">
							<option value="" selected>Unknown</option>
						</c:when>
						<c:when test="${review.isRecommended == true}">
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
					value="${review.hoursPlayed}"></p>
			<p><label for="reviewDate">Review date</label>
				<input id="reviewDate" name="reviewDate" type="date"
					value="${reviewDateText}"></p>
			<p><label for="language">Language</label>
				<input id="language" name="language" value="${fn:escapeXml(review.language)}"></p>
			<p><input type="submit" value="Update"></p>
		</form>
	</c:if>
</body>
</html>
