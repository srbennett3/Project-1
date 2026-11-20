<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Game Detail</title>
</head>
<body>
	<h1>Game Detail</h1>
	<p><a href="index.jsp">Home</a> | <a href="genres">Manage Genres</a></p>
	<p><a href="reviews">Manage Reviews</a></p>
	<p><a href="surveys">Manage Recommendation Surveys</a></p>
	<p><span style="color:red"><b>${messages.error}</b></span></p>

	<form action="gamedetail" method="get">
		<label for="appId">appId</label>
		<input id="appId" name="appId" value="${fn:escapeXml(param.appId)}">
		<input type="submit" value="Load Game">
	</form>
	<br/>

	<c:if test="${not empty game}">
		<h2><c:out value="${game.name}"/> (app_id=${game.appId})</h2>
		<p>Release date: <fmt:formatDate value="${game.releaseDate}" pattern="yyyy-MM-dd"/></p>
		<p>Price: $<c:out value="${game.price}"/></p>
		<c:if test="${not empty game.headerImage}">
			<img src="${fn:escapeXml(game.headerImage)}" width="460" alt="header"/>
		</c:if>
		<p>Metacritic: <c:out value="${game.metacriticScore}"/></p>
		<p>Positive ratings: <c:out value="${game.positiveRatings}"/>;
		   negative ratings: <c:out value="${game.negativeRatings}"/></p>
		<p>Platforms:
			<c:if test="${game.windows}">Windows </c:if>
			<c:if test="${game.mac}">Mac </c:if>
			<c:if test="${game.linux}">Linux </c:if>
		</p>
		<p>Genres:
			<c:forEach var="gg" items="${gameGenres}" varStatus="s">
				<c:out value="${gg.genre.name}"/><c:if test="${not s.last}">, </c:if>
			</c:forEach>
			(<a href="gamegenres?appId=${game.appId}">edit</a>)
		</p>
		<p>Developers:
			<c:forEach var="gd" items="${gameDevelopers}" varStatus="s">
				<c:out value="${gd.developer.name}"/><c:if test="${not s.last}">, </c:if>
			</c:forEach>
			(<a href="gamedevelopers?appId=${game.appId}">edit</a>)
		</p>
		<p>
			<a href="videocreate">Add a GameVideo</a>
			| <a href="reviews">NextQuest reviews</a>
		</p>

		<h3>Steam Recommendations</h3>
		<p><c:out value="${steamRecommendationCount}"/> recommendations for this game.
			Page <c:out value="${steamRecommendationPage}"/> of
			<c:out value="${steamRecommendationPages}"/>.</p>
		<c:choose>
			<c:when test="${empty steamRecommendations}">
				<p>No Steam recommendations for this game.</p>
			</c:when>
			<c:otherwise>
				<table border="1">
					<tr>
						<th>Recommendation ID</th>
						<th>Author ID</th>
						<th>Recommended</th>
						<th>Hours played</th>
						<th>Date</th>
					</tr>
					<c:forEach var="r" items="${steamRecommendations}">
						<tr>
							<td><c:out value="${r.steamRecommendationId}"/></td>
							<td><c:out value="${r.authorId}"/></td>
							<td><c:out value="${r.isRecommended}"/></td>
							<td><c:out value="${r.hoursPlayed}"/></td>
							<td><fmt:formatDate value="${r.recommendationDate}" pattern="yyyy-MM-dd"/></td>
						</tr>
					</c:forEach>
				</table>
				<p>
					<c:if test="${steamRecommendationPage > 1}">
						<a href="gamedetail?appId=${game.appId}&amp;recommendationPage=${steamRecommendationPage - 1}&amp;reviewPage=${steamReviewPage}">Previous recommendations</a>
					</c:if>
					<c:if test="${steamRecommendationPage < steamRecommendationPages}">
						<a href="gamedetail?appId=${game.appId}&amp;recommendationPage=${steamRecommendationPage + 1}&amp;reviewPage=${steamReviewPage}">Next recommendations</a>
					</c:if>
				</p>
			</c:otherwise>
		</c:choose>

		<h3>Steam Reviews</h3>
		<p><c:out value="${steamReviewCount}"/> reviews for this game.
			Page <c:out value="${steamReviewPage}"/> of <c:out value="${steamReviewPages}"/>.</p>
		<c:choose>
			<c:when test="${empty steamReviews}">
				<p>No Steam reviews for this game.</p>
			</c:when>
			<c:otherwise>
				<table border="1">
					<tr>
						<th>Review ID</th>
						<th>Author Steam ID</th>
						<th>Recommended</th>
						<th>Hours played</th>
						<th>Date</th>
						<th>Language</th>
					</tr>
					<c:forEach var="r" items="${steamReviews}">
						<tr>
							<td><c:out value="${r.steamReviewId}"/></td>
							<td><c:out value="${r.authorSteamId}"/></td>
							<td><c:out value="${r.isRecommended}"/></td>
							<td><c:out value="${r.hoursPlayed}"/></td>
							<td><fmt:formatDate value="${r.reviewDate}" pattern="yyyy-MM-dd"/></td>
							<td><c:out value="${r.language}"/></td>
						</tr>
					</c:forEach>
				</table>
				<p>
					<c:if test="${steamReviewPage > 1}">
						<a href="gamedetail?appId=${game.appId}&amp;reviewPage=${steamReviewPage - 1}&amp;recommendationPage=${steamRecommendationPage}">Previous reviews</a>
					</c:if>
					<c:if test="${steamReviewPage < steamReviewPages}">
						<a href="gamedetail?appId=${game.appId}&amp;reviewPage=${steamReviewPage + 1}&amp;recommendationPage=${steamRecommendationPage}">Next reviews</a>
					</c:if>
				</p>
			</c:otherwise>
		</c:choose>

		<h3>Trailers</h3>
		<c:choose>
			<c:when test="${empty trailers}">
				<p>No trailers.</p>
			</c:when>
			<c:otherwise>
				<ul>
					<c:forEach var="v" items="${trailers}">
						<li>
							<c:out value="${v.name}"/>
							<a href="gamedetail?appId=${game.appId}&videoId=${v.videoId}">Play</a>
							| <a href="videoupdate?videoId=${v.videoId}">Update</a>
							| <a href="videodelete?videoId=${v.videoId}">Delete</a>
						</li>
					</c:forEach>
				</ul>
			</c:otherwise>
		</c:choose>

		<h3>Screenshots</h3>
		<c:choose>
			<c:when test="${empty screenshots}">
				<p>No screenshots.</p>
			</c:when>
			<c:otherwise>
				<c:forEach var="s" items="${screenshots}">
					<c:if test="${not empty s.url}">
						<img src="${fn:escapeXml(s.url)}" width="460" alt="${fn:escapeXml(s.name)}"/>
						<p><c:out value="${s.name}"/></p>
					</c:if>
				</c:forEach>
			</c:otherwise>
		</c:choose>

		<h3>Watch Trailer (HLS)</h3>
		<c:choose>
			<c:when test="${not empty selectedVideo and not empty selectedVideo.videoUrl}">
				<p><b><c:out value="${selectedVideo.name}"/></b></p>
				<video id="video" controls width="800"></video>
				<script src="https://cdn.jsdelivr.net/npm/hls.js@1"></script>
				<script>
					var video = document.getElementById('video');
					var videoSrc = "${fn:escapeXml(selectedVideo.videoUrl)}";
					if (Hls.isSupported()) {
						var hls = new Hls();
						hls.loadSource(videoSrc);
						hls.attachMedia(video);
					} else if (video.canPlayType('application/vnd.apple.mpegurl')) {
						video.src = videoSrc;
					}
				</script>
			</c:when>
			<c:otherwise>
				<p>No playable trailer video_url found for this game.</p>
			</c:otherwise>
		</c:choose>
	</c:if>
</body>
</html>
