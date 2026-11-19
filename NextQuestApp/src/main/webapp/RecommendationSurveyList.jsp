<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Recommendation Surveys</title>
</head>
<body style="background-color:white;color:black;">
	<h1>Recommendation Surveys</h1>
	<p><a href="gamedetail">Back to Game Detail</a></p>
	<p><a href="reviews">View Reviews</a></p>
	<p><span style="color:green"><b>${messages.success}</b></span></p>
	<p><span style="color:red"><b>${messages.error}</b></span></p>

	<h2>Create Recommendation Survey</h2>
	<form action="surveys" method="post">
		<p><label for="userId">User user_id (required)</label>
			<input id="userId" name="userId" type="number" required></p>
		<p><label for="preferredGenre">Preferred genre</label>
			<input id="preferredGenre" name="preferredGenre"></p>
		<p><label for="preferredPlatform">Preferred platform</label>
			<input id="preferredPlatform" name="preferredPlatform"></p>
		<p><label for="playStyle">Play style</label>
			<input id="playStyle" name="playStyle"></p>
		<p><input type="submit" value="Create"></p>
	</form>

	<h2>All Recommendation Surveys</h2>
	<c:choose>
		<c:when test="${empty surveys}">
			<p>No recommendation surveys found.</p>
		</c:when>
		<c:otherwise>
			<table border="1">
				<tr>
					<th>survey_id</th>
					<th>user_id</th>
					<th>preferred_genre</th>
					<th>preferred_platform</th>
					<th>play_style</th>
					<th>updated_at</th>
					<th></th>
					<th></th>
				</tr>
				<c:forEach var="s" items="${surveys}">
					<tr>
						<td><c:out value="${s.surveyId}"/></td>
						<td><c:out value="${s.user.userId}"/></td>
						<td><c:out value="${s.preferredGenre}"/></td>
						<td><c:out value="${s.preferredPlatform}"/></td>
						<td><c:out value="${s.playStyle}"/></td>
						<td><fmt:formatDate value="${s.updatedAt}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
						<td><a href="surveyupdate?surveyId=${s.surveyId}">Update</a></td>
						<td><a href="surveydelete?surveyId=${s.surveyId}">Delete</a></td>
					</tr>
				</c:forEach>
			</table>
		</c:otherwise>
	</c:choose>
</body>
</html>
