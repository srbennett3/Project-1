<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Update Developer</title>
</head>
<body>
	<h1>Update Developer</h1>
	<p><span style="color:green"><b>${messages.success}</b></span></p>
	<p><span style="color:red"><b>${messages.error}</b></span></p>

	<c:if test="${not empty developer}">
		<form action="developerupdate" method="post">
			<input type="hidden" name="developerId" value="${developer.developerId}">
			<p>
				<label for="name">Developer Name</label>
				<input id="name" name="name" value="<c:out value='${developer.name}'/>">
			</p>
			<p><input type="submit" value="Update"></p>
		</form>
	</c:if>

	<p><a href="developers">Back to Developers</a></p>
</body>
</html>
