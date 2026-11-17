<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Delete Steam Review</title>
</head>
<body>
	<h1>Delete Steam Review</h1>
	<p><a href="steamreviews">Back to Steam Reviews</a></p>
	<p><span style="color:green"><b>${messages.success}</b></span></p>
	<p><span style="color:red"><b>${messages.error}</b></span></p>

	<form action="steamreviewdelete" method="get">
		<label for="steamReviewId">steam_review_id</label>
		<input id="steamReviewId" name="steamReviewId"
			value="${fn:escapeXml(param.steamReviewId)}">
		<input type="submit" value="Load">
	</form>

	<c:if test="${not empty review}">
		<p>Delete review id=<c:out value="${review.steamReviewId}"/>
			for app_id=<c:out value="${review.game.appId}"/>?</p>
		<form action="steamreviewdelete" method="post">
			<input type="hidden" name="steamReviewId" value="${review.steamReviewId}">
			<input type="submit" value="Delete">
		</form>
	</c:if>
</body>
</html>
