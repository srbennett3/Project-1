<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Steam Reviews</title>
</head>
<body>
	<h1>Steam Reviews</h1>
	<p><a href="steamrecommendations">View Steam Recommendations</a></p>
	<p><span style="color:green"><b>${messages.success}</b></span></p>
	<p><span style="color:red"><b>${messages.error}</b></span></p>

	<h2>Create Steam Review</h2>
	<form action="steamreviews" method="post">
		<p><label for="appId">Game app_id (required)</label>
			<input id="appId" name="appId" type="number" required></p>
		<p><label for="authorSteamId">Author Steam ID</label>
			<input id="authorSteamId" name="authorSteamId" type="number"></p>
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
		<p><label for="language">Language</label>
			<input id="language" name="language"></p>
		<p><input type="submit" value="Create"></p>
	</form>

	<h2>All Steam Reviews</h2>
	<p>Showing page <c:out value="${currentPage}"/> of <c:out value="${totalPages}"/>
		(<c:out value="${totalRecords}"/> records total).</p>
	<c:choose>
		<c:when test="${empty reviews}">
			<p>No Steam reviews found.</p>
		</c:when>
		<c:otherwise>
			<table border="1">
				<tr>
					<th>steam_review_id</th>
					<th>app_id</th>
					<th>author_steamid</th>
					<th>is_recommended</th>
					<th>hours_played</th>
					<th>review_date</th>
					<th>language</th>
					<th></th>
					<th></th>
				</tr>
				<c:forEach var="r" items="${reviews}">
					<tr>
						<td><c:out value="${r.steamReviewId}"/></td>
						<td><c:out value="${r.game.appId}"/></td>
						<td><c:out value="${r.authorSteamId}"/></td>
						<td><c:out value="${r.isRecommended}"/></td>
						<td><c:out value="${r.hoursPlayed}"/></td>
						<td><fmt:formatDate value="${r.reviewDate}" pattern="yyyy-MM-dd"/></td>
						<td><c:out value="${r.language}"/></td>
						<td><a href="steamreviewupdate?steamReviewId=${r.steamReviewId}">Update</a></td>
						<td><a href="steamreviewdelete?steamReviewId=${r.steamReviewId}">Delete</a></td>
					</tr>
				</c:forEach>
			</table>
			<p>
				<c:if test="${currentPage > 1}">
					<a href="steamreviews?page=${currentPage - 1}">Previous</a>
				</c:if>
				<c:if test="${currentPage < totalPages}">
					<a href="steamreviews?page=${currentPage + 1}">Next</a>
				</c:if>
			</p>
		</c:otherwise>
	</c:choose>
</body>
</html>
