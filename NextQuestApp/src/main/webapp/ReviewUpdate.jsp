<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Update Review</title>
</head>
<body style="background-color:white;color:black;">
	<h1>Update Review</h1>
	<p><a href="reviews">Back to Reviews</a></p>
	<p><span style="color:green"><b>${messages.success}</b></span></p>
	<p><span style="color:red"><b>${messages.error}</b></span></p>

	<form action="reviewupdate" method="get">
		<label for="reviewId">review_id</label>
		<input id="reviewId" name="reviewId" value="${fn:escapeXml(param.reviewId)}">
		<input type="submit" value="Load">
	</form>

	<c:if test="${not empty review}">
		<fmt:formatDate value="${review.reviewDate}" pattern="yyyy-MM-dd" timeZone="UTC" var="reviewDateText"/>
		<form action="reviewupdate" method="post">
			<input type="hidden" name="reviewId" value="${review.reviewId}">
			<p><label for="appId">Game app_id</label>
				<input id="appId" name="appId" type="number" required
					value="${review.game.appId}"></p>
			<p><label for="userId">User user_id</label>
				<input id="userId" name="userId" type="number" required
					value="${review.user.userId}"></p>
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
			<p><input type="submit" value="Update"></p>
		</form>
	</c:if>
</body>
</html>
