<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Delete Recommendation Survey</title>
</head>
<body style="background-color:white;color:black;">
	<h1>Delete Recommendation Survey</h1>
	<p><a href="surveys">Back to Recommendation Surveys</a></p>
	<p><span style="color:green"><b>${messages.success}</b></span></p>
	<p><span style="color:red"><b>${messages.error}</b></span></p>

	<form action="surveydelete" method="get">
		<label for="surveyId">survey_id</label>
		<input id="surveyId" name="surveyId" value="${fn:escapeXml(param.surveyId)}">
		<input type="submit" value="Load">
	</form>

	<c:if test="${not empty survey}">
		<p>Delete survey id=<c:out value="${survey.surveyId}"/>
			for user_id=<c:out value="${survey.user.userId}"/>?</p>
		<form action="surveydelete" method="post">
			<input type="hidden" name="surveyId" value="${survey.surveyId}">
			<input type="submit" value="Delete">
		</form>
	</c:if>
</body>
</html>
