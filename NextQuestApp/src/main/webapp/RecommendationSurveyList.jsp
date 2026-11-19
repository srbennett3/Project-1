<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
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
	<c:if test="${not empty createdSurvey}">
		<p><a href="recommendations?surveyId=${createdSurvey.surveyId}">See recommended games for this survey</a></p>
	</c:if>

	<h2>Create Recommendation Survey</h2>
	<form action="surveys" method="post">
		<c:if test="${not empty filterUserId}">
			<input type="hidden" name="filterUserId" value="${filterUserId}">
		</c:if>
		<p><label for="userId">User (required)</label>
			<c:choose>
				<c:when test="${empty users}">
					No users yet. <a href="users">Create a user first.</a>
				</c:when>
				<c:otherwise>
					<select id="userId" name="userId" required>
						<option value="">-- choose a user --</option>
						<c:forEach var="u" items="${users}">
							<option value="${u.userId}" ${u.userId == selectedUserId ? 'selected' : ''}><c:out value="${u.username}"/> (user_id <c:out value="${u.userId}"/>)</option>
						</c:forEach>
					</select>
				</c:otherwise>
			</c:choose>
		</p>
		<p><label for="preferredGenre">Preferred genre</label>
			<select id="preferredGenre" name="preferredGenre">
				<option value="">Any genre</option>
				<c:forEach var="g" items="${genres}">
					<option value="${fn:escapeXml(g)}"><c:out value="${g}"/></option>
				</c:forEach>
			</select></p>
		<p><label for="preferredPlatform">Preferred platform</label>
			<select id="preferredPlatform" name="preferredPlatform">
				<option value="">Any platform</option>
				<c:forEach var="p" items="${platforms}">
					<option value="${p}"><c:out value="${p}"/></option>
				</c:forEach>
			</select></p>
		<p><label for="playStyle">Play style</label>
			<select id="playStyle" name="playStyle">
				<option value="">No preference</option>
				<c:forEach var="s" items="${playStyles}">
					<option value="${s}"><c:out value="${s}"/></option>
				</c:forEach>
			</select>
			<small>(free = price 0; budget = under $10; acclaimed = Metacritic 80+)</small></p>
		<p><input type="submit" value="Create" ${empty users ? 'disabled' : ''}></p>
	</form>

	<h2>
		<c:choose>
			<c:when test="${not empty filterUserId}">Recommendation Surveys of <c:out value="${usernames[filterUserId]}"/> (user_id <c:out value="${filterUserId}"/>)</c:when>
			<c:otherwise>All Recommendation Surveys</c:otherwise>
		</c:choose>
	</h2>
	<c:if test="${not empty filterUserId}">
		<p><a href="surveys">Show all surveys</a></p>
	</c:if>
	<c:choose>
		<c:when test="${empty surveys}">
			<p>No recommendation surveys found.</p>
		</c:when>
		<c:otherwise>
			<table border="1">
				<tr>
					<th>survey_id</th>
					<th>user_id</th>
					<th>username</th>
					<th>preferred_genre</th>
					<th>preferred_platform</th>
					<th>play_style</th>
					<th>updated_at</th>
					<th></th>
					<th></th>
					<th></th>
				</tr>
				<c:forEach var="s" items="${surveys}">
					<tr>
						<td><c:out value="${s.surveyId}"/></td>
						<td><c:out value="${s.user.userId}"/></td>
						<td><c:out value="${usernames[s.user.userId]}"/></td>
						<td><c:out value="${s.preferredGenre}"/></td>
						<td><c:out value="${s.preferredPlatform}"/></td>
						<td><c:out value="${s.playStyle}"/></td>
						<td><fmt:formatDate value="${s.updatedAt}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
						<td><a href="recommendations?surveyId=${s.surveyId}">Recommendations</a></td>
						<td><a href="surveyupdate?surveyId=${s.surveyId}">Update</a></td>
						<td><a href="surveydelete?surveyId=${s.surveyId}">Delete</a></td>
					</tr>
				</c:forEach>
			</table>
		</c:otherwise>
	</c:choose>
</body>
</html>
