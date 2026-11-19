<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Delete Review</title>
</head>
<body style="background-color:white;color:black;">
	<h1>Delete Review</h1>
	<p><a href="reviews">Back to Reviews</a></p>
	<p><span style="color:green"><b>${messages.success}</b></span></p>
	<p><span style="color:red"><b>${messages.error}</b></span></p>

	<form action="reviewdelete" method="get">
		<label for="reviewId">review_id</label>
		<input id="reviewId" name="reviewId" value="${fn:escapeXml(param.reviewId)}">
		<input type="submit" value="Load">
	</form>

	<c:if test="${not empty review}">
		<p>Delete review id=<c:out value="${review.reviewId}"/>
			for app_id=<c:out value="${review.game.appId}"/>
			and user_id=<c:out value="${review.user.userId}"/>?</p>
		<form action="reviewdelete" method="post">
			<input type="hidden" name="reviewId" value="${review.reviewId}">
			<input type="submit" value="Delete">
		</form>
	</c:if>
</body>
</html>
