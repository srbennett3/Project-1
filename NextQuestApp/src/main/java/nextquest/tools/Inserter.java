package nextquest.tools;

import nextquest.dal.GameDao;
import nextquest.dal.GameVideoDao;
import nextquest.dal.GenreDao;
import nextquest.model.Game;
import nextquest.model.GameVideo;
import nextquest.model.Genre;
import nextquest.model.VideoType;
import nextquest.dal.UserDao;
import nextquest.dal.DeveloperDao;
import nextquest.model.User;
import nextquest.model.Developer;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * main() runner to exercise CRUD on Game, Genre, and GameVideo.
 * Run it, verify the output, then delete the test rows it created
 * (the last block already removes them so the DB stays clean).
 */
public class Inserter {

	public static void main(String[] args) throws SQLException {
		GameDao gameDao = GameDao.getInstance();
		GenreDao genreDao = GenreDao.getInstance();
		GameVideoDao gameVideoDao = GameVideoDao.getInstance();
		UserDao userDao = UserDao.getInstance();
		DeveloperDao developerDao = DeveloperDao.getInstance();

		// CREATE / READ / UPDATE / DELETE a Genre.
		Genre genre = new Genre("TestGenre");
		genre = genreDao.create(genre);
		System.out.format("Created genre: id=%d name=%s%n",
			genre.getGenreId(), genre.getName());

		Genre readGenre = genreDao.getGenreById(genre.getGenreId());
		System.out.format("Read genre: id=%d name=%s%n",
			readGenre.getGenreId(), readGenre.getName());

		genre = genreDao.updateName(genre, "TestGenreUpdated");
		System.out.format("Updated genre: id=%d name=%s%n",
			genre.getGenreId(), genre.getName());

		genreDao.delete(genre);
		System.out.format("Deleted genre id=%d%n", genre.getGenreId());

		// CREATE / READ a Game.
		Game game = new Game("TestGame", new Date(), new BigDecimal("9.99"),
			null, 80, 100, 5, true, false, false);
		game = gameDao.create(game);
		System.out.format("Created game: app_id=%d name=%s%n",
			game.getAppId(), game.getName());

		Game readGame = gameDao.getGameById(game.getAppId());
		System.out.format("Read game: app_id=%d name=%s price=%s%n",
			readGame.getAppId(), readGame.getName(), readGame.getPrice());

		// CREATE / READ a GameVideo (trailer).
		GameVideo video = new GameVideo(game, "TestTrailer", null,
			"https://example.com/trailer.m3u8", VideoType.TRAILER);
		video = gameVideoDao.create(video);
		System.out.format("Created video: video_id=%d app_id=%d type=%s%n",
			video.getVideoId(), video.getGame().getAppId(), video.getVideoType());

		GameVideo readVideo = gameVideoDao.getGameVideoById(video.getVideoId());
		System.out.format("Read video: video_id=%d name=%s video_url=%s%n",
			readVideo.getVideoId(), readVideo.getName(), readVideo.getVideoUrl());

		// UPDATE video name.
		video = gameVideoDao.updateName(video, "TestTrailerUpdated");
		System.out.format("Updated video name=%s%n", video.getName());

		// READ videos by app_id and type.
		List<GameVideo> trailers = gameVideoDao.getVideosByAppIdAndType(
			game.getAppId(), VideoType.TRAILER);
		System.out.format("Trailers for app_id=%d: %d%n", game.getAppId(), trailers.size());

		// Clean up: delete video then game.
		gameVideoDao.delete(video);
		gameDao.delete(game);
		System.out.println("Cleaned up test GameVideo and Game.");
		

		// CREATE / READ / UPDATE / DELETE a User.
		User user = new User("NextQuestTestUser", "test@example.com");
		user = userDao.create(user);
		System.out.format("Created user: id=%d username=%s%n",
			user.getUserId(), user.getUsername());

		User readUser = userDao.getUserById(user.getUserId());
		System.out.format("Read user: id=%d username=%s email=%s%n",
			readUser.getUserId(), readUser.getUsername(), readUser.getEmail());

		user = userDao.updateEmail(user, "updated@example.com");
		System.out.format("Updated user email=%s%n", user.getEmail());

		userDao.delete(user);
		System.out.format("Deleted user id=%d%n", user.getUserId());

		// CREATE / READ / UPDATE / DELETE a Developer.
		Developer developer = new Developer("NextQuestTestDeveloper");
		developer = developerDao.create(developer);
		System.out.format("Created developer: id=%d name=%s%n",
			developer.getDeveloperId(), developer.getName());

		Developer readDeveloper = developerDao.getDeveloperById(developer.getDeveloperId());
		System.out.format("Read developer: id=%d name=%s%n",
			readDeveloper.getDeveloperId(), readDeveloper.getName());

		developer = developerDao.updateName(developer, "NextQuestTestDeveloperUpdated");
		System.out.format("Updated developer name=%s%n", developer.getName());

		developerDao.delete(developer);
		System.out.format("Deleted developer id=%d%n", developer.getDeveloperId());

	}
}
