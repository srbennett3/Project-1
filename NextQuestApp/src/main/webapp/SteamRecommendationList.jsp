<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Steam Recommendations</title>
</head>
<body>
	<h1>Steam Recommendations</h1>
	<p><a href="steamreviews">View Steam Reviews</a></p>
	<p><span style="color:green"><b>${messages.success}</b></span></p>
	<p><span style="color:red"><b>${messages.error}</b></span></p>

	<h2>Create Steam Recommendation</h2>
	<form action="steamrecommendations" method="post">
		<p><label for="appId">Game app_id (required)</label>
			<input id="appId" name="appId" type="number" required></p>
		<p><label for="authorId">Steam author_id</label>
			<input id="authorId" name="authorId" type="number"></p>
		<p><label for="isRecommended">Recommended</label>
			<select id="isRecommended" name="isRecommended">
				<option value="">Unknown</option>
				<option value="true">Yes</option>
				<option value="false">No</option>
			</select></p>
		<p><label for="hoursPlayed">Hours played</label>
			<input id="hoursPlayed" name="hoursPlayed" type="number" step="0.01"></p>
		<p><label for="recommendationDate">Recommendation date</label>
			<input id="recommendationDate" name="recommendationDate" type="date"></p>
		<p><input type="submit" value="Create"></p>
	</form>

	<h2>All Steam Recommendations</h2>
	<p>Showing page <c:out value="${currentPage}"/> of <c:out value="${totalPages}"/>
		(<c:out value="${totalRecords}"/> records total).</p>
	<c:choose>
		<c:when test="${empty recommendations}">
			<p>No Steam recommendations found.</p>
		</c:when>
		<c:otherwise>
			<table border="1">
				<tr>
					<th>steam_recommendation_id</th>
					<th>app_id</th>
					<th>author_id</th>
					<th>is_recommended</th>
					<th>hours_played</th>
					<th>recommendation_date</th>
					<th></th>
					<th></th>
				</tr>
				<c:forEach var="r" items="${recommendations}">
					<tr>
						<td><c:out value="${r.steamRecommendationId}"/></td>
						<td><c:out value="${r.game.appId}"/></td>
						<td><c:out value="${r.authorId}"/></td>
						<td><c:out value="${r.isRecommended}"/></td>
						<td><c:out value="${r.hoursPlayed}"/></td>
						<td><fmt:formatDate value="${r.recommendationDate}" pattern="yyyy-MM-dd"/></td>
						<td><a href="steamrecommendationupdate?steamRecommendationId=${r.steamRecommendationId}">Update</a></td>
						<td><a href="steamrecommendationdelete?steamRecommendationId=${r.steamRecommendationId}">Delete</a></td>
					</tr>
				</c:forEach>
			</table>
			<p>
				<c:if test="${currentPage > 1}">
					<a href="steamrecommendations?page=${currentPage - 1}">Previous</a>
				</c:if>
				<c:if test="${currentPage < totalPages}">
					<a href="steamrecommendations?page=${currentPage + 1}">Next</a>
				</c:if>
			</p>
		</c:otherwise>
	</c:choose>
</body>
</html>
