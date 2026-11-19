<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Reviews</title>
</head>
<body style="background-color:white;color:black;">
	<h1>Reviews</h1>
	<p><a href="gamedetail">Back to Game Detail</a></p>
	<p><a href="surveys">View Recommendation Surveys</a></p>
	<p><span style="color:green"><b>${messages.success}</b></span></p>
	<p><span style="color:red"><b>${messages.error}</b></span></p>

	<h2>Create Review</h2>
	<form action="reviews" method="post">
		<p><label for="appId">Game app_id (required)</label>
			<input id="appId" name="appId" type="number" required></p>
		<p><label for="userId">User user_id (required)</label>
			<input id="userId" name="userId" type="number" required></p>
		<p><label for="isRecommended">Recommended</label>
			<select id="isRecommended" name="isRecommended">
				<option value="">Unknown</option>
				<option value="true">Yes</option>
				<option value="false">No</option>
			</select></p>
		<p><label for="hoursPlayed">Hours played</label>
			<input id="hoursPlayed" name="hoursPlayed" type="number" step="0.01"></p>
		<p><label for="reviewDate">Review date</label>
			<input id="reviewDate" name="reviewDate" type="date"></p>
		<p><input type="submit" value="Create"></p>
	</form>

	<h2>All Reviews</h2>
	<c:choose>
		<c:when test="${empty reviews}">
			<p>No reviews found.</p>
		</c:when>
		<c:otherwise>
			<table border="1">
				<tr>
					<th>review_id</th>
					<th>app_id</th>
					<th>user_id</th>
					<th>is_recommended</th>
					<th>hours_played</th>
					<th>review_date</th>
					<th></th>
					<th></th>
				</tr>
				<c:forEach var="r" items="${reviews}">
					<tr>
						<td><c:out value="${r.reviewId}"/></td>
						<td><c:out value="${r.game.appId}"/></td>
						<td><c:out value="${r.user.userId}"/></td>
						<td><c:out value="${r.isRecommended}"/></td>
						<td><c:out value="${r.hoursPlayed}"/></td>
						<td><fmt:formatDate value="${r.reviewDate}" pattern="yyyy-MM-dd" timeZone="UTC"/></td>
						<td><a href="reviewupdate?reviewId=${r.reviewId}">Update</a></td>
						<td><a href="reviewdelete?reviewId=${r.reviewId}">Delete</a></td>
					</tr>
				</c:forEach>
			</table>
		</c:otherwise>
	</c:choose>
</body>
</html>
