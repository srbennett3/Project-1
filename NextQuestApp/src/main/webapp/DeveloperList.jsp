<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Developers</title>
</head>
<body>
	<h1>Developers</h1>
	<p><span style="color:green"><b>${messages.success}</b></span></p>
	<p><span style="color:red"><b>${messages.error}</b></span></p>

	<h2>Create Developer</h2>
	<form action="developers" method="post">
		<p>
			<label for="name">Name</label>
			<input id="name" name="name" value="">
		</p>
		<p><input type="submit" value="Create"></p>
	</form>

	<h2>All Developers</h2>
	<c:choose>
		<c:when test="${empty developers}">
			<p>No developers.</p>
		</c:when>
		<c:otherwise>
			<table border="1">
				<tr><th>developer_id</th><th>name</th><th></th><th></th><th></th></tr>
				<c:forEach var="d" items="${developers}">
					<tr>
						<td><c:out value="${d.developerId}"/></td>
						<td><c:out value="${d.name}"/></td>
						<td><a href="developerupdate?developerId=${d.developerId}">Update</a></td>
						<td><a href="developerdelete?developerId=${d.developerId}">Delete</a></td>
						<td><a href="developergames?developerId=${d.developerId}">Games</a></td>
					</tr>
				</c:forEach>
			</table>
		</c:otherwise>
	</c:choose>
</body>
</html>
