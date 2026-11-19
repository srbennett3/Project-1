<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Update Recommendation Survey</title>
</head>
<body style="background-color:white;color:black;">
	<h1>Update Recommendation Survey</h1>
	<p><a href="surveys">Back to Recommendation Surveys</a></p>
	<p><span style="color:green"><b>${messages.success}</b></span></p>
	<p><span style="color:red"><b>${messages.error}</b></span></p>

	<form action="surveyupdate" method="get">
		<label for="surveyId">survey_id</label>
		<input id="surveyId" name="surveyId" value="${fn:escapeXml(param.surveyId)}">
		<input type="submit" value="Load">
	</form>

	<c:if test="${not empty survey}">
		<%-- An older survey may hold a free-text answer that is not on a list;
		     it is shown as an extra option so saving keeps it. --%>
		<c:set var="genreListed" value="${empty survey.preferredGenre}"/>
		<c:forEach var="g" items="${genres}">
			<c:if test="${g == survey.preferredGenre}"><c:set var="genreListed" value="true"/></c:if>
		</c:forEach>
		<c:set var="platformListed" value="${empty survey.preferredPlatform}"/>
		<c:forEach var="p" items="${platforms}">
			<c:if test="${p == survey.preferredPlatform}"><c:set var="platformListed" value="true"/></c:if>
		</c:forEach>
		<c:set var="styleListed" value="${empty survey.playStyle}"/>
		<c:forEach var="s" items="${playStyles}">
			<c:if test="${s == survey.playStyle}"><c:set var="styleListed" value="true"/></c:if>
		</c:forEach>

		<form action="surveyupdate" method="post">
			<input type="hidden" name="surveyId" value="${survey.surveyId}">
			<p><label for="userId">User</label>
				<select id="userId" name="userId" required>
					<c:forEach var="u" items="${users}">
						<option value="${u.userId}" ${u.userId == survey.user.userId ? 'selected' : ''}><c:out value="${u.username}"/> (user_id <c:out value="${u.userId}"/>)</option>
					</c:forEach>
				</select></p>
			<p><label for="preferredGenre">Preferred genre</label>
				<select id="preferredGenre" name="preferredGenre">
					<option value="">Any genre</option>
					<c:if test="${not genreListed}">
						<option value="${fn:escapeXml(survey.preferredGenre)}" selected><c:out value="${survey.preferredGenre}"/></option>
					</c:if>
					<c:forEach var="g" items="${genres}">
						<option value="${fn:escapeXml(g)}" ${g == survey.preferredGenre ? 'selected' : ''}><c:out value="${g}"/></option>
					</c:forEach>
				</select></p>
			<p><label for="preferredPlatform">Preferred platform</label>
				<select id="preferredPlatform" name="preferredPlatform">
					<option value="">Any platform</option>
					<c:if test="${not platformListed}">
						<option value="${fn:escapeXml(survey.preferredPlatform)}" selected><c:out value="${survey.preferredPlatform}"/></option>
					</c:if>
					<c:forEach var="p" items="${platforms}">
						<option value="${p}" ${p == survey.preferredPlatform ? 'selected' : ''}><c:out value="${p}"/></option>
					</c:forEach>
				</select></p>
			<p><label for="playStyle">Play style</label>
				<select id="playStyle" name="playStyle">
					<option value="">No preference</option>
					<c:if test="${not styleListed}">
						<option value="${fn:escapeXml(survey.playStyle)}" selected><c:out value="${survey.playStyle}"/></option>
					</c:if>
					<c:forEach var="s" items="${playStyles}">
						<option value="${s}" ${s == survey.playStyle ? 'selected' : ''}><c:out value="${s}"/></option>
					</c:forEach>
				</select>
				<small>(free = price 0; budget = under $10; acclaimed = Metacritic 80+)</small></p>
			<p>updated_at: <fmt:formatDate value="${survey.updatedAt}" pattern="yyyy-MM-dd HH:mm:ss"/></p>
			<p><input type="submit" value="Update"></p>
		</form>
		<p><a href="recommendations?surveyId=${survey.surveyId}">See recommended games for this survey</a></p>
	</c:if>
</body>
</html>
