<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Recommended Games</title>
</head>
<body style="background-color:white;color:black;">
	<h1>Recommended Games</h1>
	<p><a href="surveys">Back to Recommendation Surveys</a></p>
	<p><span style="color:red"><b>${messages.error}</b></span></p>

	<c:if test="${not empty survey}">
		<p>
			Survey id=<c:out value="${survey.surveyId}"/> for
			<c:choose>
				<c:when test="${empty survey.user.username}">user_id <c:out value="${survey.user.userId}"/></c:when>
				<c:otherwise><c:out value="${survey.user.username}"/></c:otherwise>
			</c:choose>:
			genre <b><c:out value="${empty survey.preferredGenre ? 'any' : survey.preferredGenre}"/></b>,
			platform <b><c:out value="${empty survey.preferredPlatform ? 'any' : survey.preferredPlatform}"/></b>,
			play style <b><c:out value="${empty survey.playStyle ? 'any' : survey.playStyle}"/></b>.
			<a href="surveyupdate?surveyId=${survey.surveyId}">Change answers</a>
		</p>
		<p>Ranked by share of positive Steam ratings (games with at least 50 ratings),
		   leaving out games this user already saved or reviewed.</p>

		<c:choose>
			<c:when test="${empty games}">
				<p>No games match this survey. Try a wider genre, platform, or play style.</p>
			</c:when>
			<c:otherwise>
				<table border="1">
					<tr>
						<th>#</th>
						<th></th>
						<th>Game</th>
						<th>Released</th>
						<th>Price</th>
						<th>Positive</th>
						<th>Metacritic</th>
						<th></th>
					</tr>
					<c:forEach var="g" items="${games}" varStatus="row">
						<tr>
							<td>${row.count}</td>
							<td>
								<c:if test="${not empty g.headerImage}">
									<img src="${fn:escapeXml(g.headerImage)}" width="184" alt="">
								</c:if>
							</td>
							<td><a href="gamedetail?appId=${g.appId}"><c:out value="${g.name}"/></a></td>
							<td><fmt:formatDate value="${g.releaseDate}" pattern="yyyy-MM-dd"/></td>
							<td>$<c:out value="${g.price}"/></td>
							<td>
								<fmt:formatNumber value="${g.positiveRatings / (g.positiveRatings + g.negativeRatings)}"
									type="percent" maxFractionDigits="0"/>
								of <c:out value="${g.positiveRatings + g.negativeRatings}"/>
							</td>
							<td><c:out value="${g.metacriticScore}"/></td>
							<td>
								<form action="savedgames" method="post">
									<input type="hidden" name="userId" value="${survey.user.userId}">
									<input type="hidden" name="appId" value="${g.appId}">
									<input type="submit" value="Save">
								</form>
							</td>
						</tr>
					</c:forEach>
				</table>
			</c:otherwise>
		</c:choose>
	</c:if>
</body>
</html>
