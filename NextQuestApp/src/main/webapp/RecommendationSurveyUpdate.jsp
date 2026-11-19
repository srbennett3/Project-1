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
		<form action="surveyupdate" method="post">
			<input type="hidden" name="surveyId" value="${survey.surveyId}">
			<p><label for="userId">User user_id</label>
				<input id="userId" name="userId" type="number" required
					value="${survey.user.userId}"></p>
			<p><label for="preferredGenre">Preferred genre</label>
				<input id="preferredGenre" name="preferredGenre"
					value="${fn:escapeXml(survey.preferredGenre)}"></p>
			<p><label for="preferredPlatform">Preferred platform</label>
				<input id="preferredPlatform" name="preferredPlatform"
					value="${fn:escapeXml(survey.preferredPlatform)}"></p>
			<p><label for="playStyle">Play style</label>
				<input id="playStyle" name="playStyle"
					value="${fn:escapeXml(survey.playStyle)}"></p>
			<p>updated_at: <fmt:formatDate value="${survey.updatedAt}" pattern="yyyy-MM-dd HH:mm:ss"/></p>
			<p><input type="submit" value="Update"></p>
		</form>
	</c:if>
</body>
</html>
