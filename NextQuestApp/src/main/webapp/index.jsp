<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>NextQuest</title>
</head>
<body>
	<h1>NextQuest</h1>
	<p>Find your next game.</p>

	<h2>Games</h2>
	<form action="gamedetail" method="get">
		<label for="appId">Look up a game by appId</label>
		<input id="appId" name="appId" value="">
		<input type="submit" value="Go">
	</form>
	<ul>
		<li><a href="genres">Genres</a></li>
		<li><a href="developers">Developers</a></li>
		<li><a href="gamegenres">Genres of a game</a></li>
		<li><a href="gamedevelopers">Developers of a game</a></li>
		<li><a href="videocreate">Add a game video</a></li>
	</ul>

	<h2>Players</h2>
	<ul>
		<li><a href="users">Users</a></li>
		<li><a href="savedgames">Saved games</a></li>
		<li><a href="reviews">Reviews</a></li>
		<li><a href="surveys">Recommendation surveys</a> (take the survey, then see your recommended games)</li>
	</ul>

	<h2>Steam Data</h2>
	<ul>
		<li><a href="steamreviews">Steam reviews</a></li>
		<li><a href="steamrecommendations">Steam recommendations</a></li>
	</ul>
</body>
</html>
