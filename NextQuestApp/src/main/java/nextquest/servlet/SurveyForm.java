package nextquest.servlet;

import nextquest.dal.GenreDao;
import nextquest.dal.RecommendationSurveyDao;
import nextquest.dal.UserDao;
import nextquest.model.Genre;
import nextquest.model.User;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * Shared pieces of the survey create and update forms: the dropdown choices,
 * and checks on the submitted answers.
 */
final class SurveyForm {

	private SurveyForm() {}

	/**
	 * Put the dropdown choices on the request: users, usernames (user_id to username),
	 * genres (names), platforms, and playStyles.
	 * req is an HttpServletRequest. Returns nothing. Throws SQLException.
	 */
	static void loadChoices(HttpServletRequest req) throws SQLException {
		List<User> users = UserDao.getInstance().getAllUsers();
		Map<Integer, String> usernames = new HashMap<Integer, String>();
		for (User user : users) {
			usernames.put(user.getUserId(), user.getUsername());
		}
		List<String> genres = new ArrayList<String>();
		for (Genre genre : GenreDao.getInstance().getAllGenres()) {
			genres.add(genre.getName());
		}
		req.setAttribute("users", users);
		req.setAttribute("usernames", usernames);
		req.setAttribute("genres", genres);
		req.setAttribute("platforms", Arrays.asList(RecommendationSurveyDao.PLATFORMS));
		req.setAttribute("playStyles", Arrays.asList(RecommendationSurveyDao.PLAY_STYLES));
	}

	/**
	 * Check a genre answer. Blank means any genre.
	 * value is the submitted text. current is the survey's old answer, or null.
	 * Returns the answer, or null. Throws IllegalArgumentException when it is not offered.
	 * Throws SQLException.
	 */
	static String genre(String value, String current) throws SQLException {
		String genre = trimToNull(value);
		if (genre == null || genre.equals(current)
				|| !GenreDao.getInstance().getGenresByName(genre).isEmpty()) {
			return genre;
		}
		throw new IllegalArgumentException("Unknown genre: " + genre);
	}

	/**
	 * Check a platform answer. Blank means any platform.
	 * value is the submitted text. current is the survey's old answer, or null.
	 * Returns the answer, or null. Throws IllegalArgumentException when it is not offered.
	 */
	static String platform(String value, String current) {
		return fromList(value, current, RecommendationSurveyDao.PLATFORMS, "platform");
	}

	/**
	 * Check a play style answer. Blank means no preference.
	 * value is the submitted text. current is the survey's old answer, or null.
	 * Returns the answer, or null. Throws IllegalArgumentException when it is not offered.
	 */
	static String playStyle(String value, String current) {
		return fromList(value, current, RecommendationSurveyDao.PLAY_STYLES, "play style");
	}

	private static String fromList(String value, String current, String[] allowed, String what) {
		String answer = trimToNull(value);
		if (answer == null || answer.equals(current) || Arrays.asList(allowed).contains(answer)) {
			return answer;
		}
		throw new IllegalArgumentException("Unknown " + what + ": " + answer);
	}

	private static String trimToNull(String value) {
		return value == null || value.trim().isEmpty() ? null : value.trim();
	}
}
