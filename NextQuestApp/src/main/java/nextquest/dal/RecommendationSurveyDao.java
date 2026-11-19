package nextquest.dal;

import nextquest.model.Game;
import nextquest.model.RecommendationSurvey;
import nextquest.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * DAO for the RecommendationSurvey table, plus the query that turns a survey
 * into a list of recommended games.
 */
public class RecommendationSurveyDao {
	protected ConnectionManager connectionManager;

	private static RecommendationSurveyDao instance = null;

	/** Platforms a survey can ask for. Each one is also a BOOLEAN column on Game. */
	public static final String[] PLATFORMS = {"windows", "mac", "linux"};

	/** Play styles a survey can ask for, and how each one filters games. */
	public static final String[] PLAY_STYLES = {"any", "free", "budget", "acclaimed"};

	/** A game needs this many Steam ratings before it can be recommended. */
	private static final int MIN_RATINGS = 50;

	private static final String SELECT_COLUMNS =
		"SELECT s.survey_id,s.user_id,s.preferred_genre,s.preferred_platform," +
		"s.play_style,s.updated_at,u.username,u.email " +
		"FROM RecommendationSurvey s INNER JOIN User u ON u.user_id=s.user_id ";

	protected RecommendationSurveyDao() {
		connectionManager = new ConnectionManager();
	}

	public static RecommendationSurveyDao getInstance() {
		if (instance == null) {
			instance = new RecommendationSurveyDao();
		}
		return instance;
	}

	public RecommendationSurvey create(RecommendationSurvey survey) throws SQLException {
		String insert =
			"INSERT INTO RecommendationSurvey(user_id,preferred_genre,preferred_platform," +
			"play_style,updated_at) VALUES(?,?,?,?,?);";
		survey.setUpdatedAt(new Date());
		try (Connection connection = connectionManager.getConnection();
				PreparedStatement statement = connection.prepareStatement(insert,
					Statement.RETURN_GENERATED_KEYS)) {
			statement.setInt(1, survey.getUser().getUserId());
			statement.setString(2, survey.getPreferredGenre());
			statement.setString(3, survey.getPreferredPlatform());
			statement.setString(4, survey.getPlayStyle());
			statement.setTimestamp(5, new Timestamp(survey.getUpdatedAt().getTime()));
			statement.executeUpdate();

			try (ResultSet keys = statement.getGeneratedKeys()) {
				if (!keys.next()) {
					throw new SQLException("Unable to retrieve generated RecommendationSurvey id.");
				}
				survey.setSurveyId(keys.getInt(1));
			}
			return survey;
		}
	}

	/** Saves the survey answers and stamps updated_at with the current time. */
	public RecommendationSurvey update(RecommendationSurvey survey) throws SQLException {
		String update =
			"UPDATE RecommendationSurvey SET preferred_genre=?,preferred_platform=?," +
			"play_style=?,updated_at=? WHERE survey_id=?;";
		survey.setUpdatedAt(new Date());
		try (Connection connection = connectionManager.getConnection();
				PreparedStatement statement = connection.prepareStatement(update)) {
			statement.setString(1, survey.getPreferredGenre());
			statement.setString(2, survey.getPreferredPlatform());
			statement.setString(3, survey.getPlayStyle());
			statement.setTimestamp(4, new Timestamp(survey.getUpdatedAt().getTime()));
			statement.setInt(5, survey.getSurveyId());
			statement.executeUpdate();
			return survey;
		}
	}

	public RecommendationSurvey delete(RecommendationSurvey survey) throws SQLException {
		String delete = "DELETE FROM RecommendationSurvey WHERE survey_id=?;";
		try (Connection connection = connectionManager.getConnection();
				PreparedStatement statement = connection.prepareStatement(delete)) {
			statement.setInt(1, survey.getSurveyId());
			statement.executeUpdate();
			return null;
		}
	}

	public RecommendationSurvey getSurveyById(int surveyId) throws SQLException {
		List<RecommendationSurvey> surveys =
			queryList(SELECT_COLUMNS + "WHERE s.survey_id=?;", surveyId);
		return surveys.isEmpty() ? null : surveys.get(0);
	}

	public List<RecommendationSurvey> getSurveysByUserId(int userId) throws SQLException {
		return queryList(SELECT_COLUMNS +
			"WHERE s.user_id=? ORDER BY s.updated_at DESC,s.survey_id DESC;", userId);
	}

	public List<RecommendationSurvey> getAllSurveys() throws SQLException {
		List<RecommendationSurvey> surveys = new ArrayList<RecommendationSurvey>();
		String select = SELECT_COLUMNS + "ORDER BY s.updated_at DESC,s.survey_id DESC;";
		try (Connection connection = connectionManager.getConnection();
				PreparedStatement statement = connection.prepareStatement(select);
				ResultSet results = statement.executeQuery()) {
			while (results.next()) {
				surveys.add(extractSurvey(results));
			}
		}
		return surveys;
	}

	/**
	 * Recommends games for a survey: games in the preferred genre, on the preferred
	 * platform, matching the play style, that the user has not already saved or
	 * reviewed. Ranked by Steam positive-rating share, then by number of ratings.
	 */
	public List<Game> getRecommendedGames(RecommendationSurvey survey, int limit)
			throws SQLException {
		StringBuilder select = new StringBuilder(
			"SELECT g.app_id,g.name,g.release_date,g.price,g.header_image," +
			"g.metacritic_score,g.positive_ratings,g.negative_ratings," +
			"g.windows,g.mac,g.linux FROM Game g " +
			"WHERE g.positive_ratings + g.negative_ratings >= ? " +
			"AND g.app_id NOT IN (SELECT app_id FROM SavedGame WHERE user_id=?) " +
			"AND g.app_id NOT IN (SELECT app_id FROM Review WHERE user_id=?) ");
		List<Object> params = new ArrayList<Object>();
		params.add(MIN_RATINGS);
		params.add(survey.getUser().getUserId());
		params.add(survey.getUser().getUserId());

		String genre = survey.getPreferredGenre();
		if (genre != null && !genre.trim().isEmpty()) {
			select.append("AND EXISTS (SELECT 1 FROM GameGenre gg " +
				"INNER JOIN Genre ge ON ge.genre_id=gg.genre_id " +
				"WHERE gg.app_id=g.app_id AND ge.name=?) ");
			params.add(genre.trim());
		}

		// The platform name becomes a column name, so it must come from the whitelist.
		String platform = survey.getPreferredPlatform();
		for (String allowed : PLATFORMS) {
			if (allowed.equalsIgnoreCase(platform)) {
				select.append("AND g.").append(allowed).append("=TRUE ");
			}
		}

		String playStyle = survey.getPlayStyle();
		if ("free".equalsIgnoreCase(playStyle)) {
			select.append("AND g.price=0 ");
		} else if ("budget".equalsIgnoreCase(playStyle)) {
			select.append("AND g.price<10 ");
		} else if ("acclaimed".equalsIgnoreCase(playStyle)) {
			select.append("AND g.metacritic_score>=80 ");
		}

		select.append("ORDER BY g.positive_ratings / (g.positive_ratings + g.negative_ratings) DESC," +
			"g.positive_ratings + g.negative_ratings DESC LIMIT ?;");
		params.add(limit);

		List<Game> games = new ArrayList<Game>();
		try (Connection connection = connectionManager.getConnection();
				PreparedStatement statement = connection.prepareStatement(select.toString())) {
			for (int i = 0; i < params.size(); i++) {
				statement.setObject(i + 1, params.get(i));
			}
			try (ResultSet results = statement.executeQuery()) {
				while (results.next()) {
					games.add(extractGame(results));
				}
			}
		}
		return games;
	}

	private List<RecommendationSurvey> queryList(String select, int id) throws SQLException {
		List<RecommendationSurvey> surveys = new ArrayList<RecommendationSurvey>();
		try (Connection connection = connectionManager.getConnection();
				PreparedStatement statement = connection.prepareStatement(select)) {
			statement.setInt(1, id);
			try (ResultSet results = statement.executeQuery()) {
				while (results.next()) {
					surveys.add(extractSurvey(results));
				}
			}
		}
		return surveys;
	}

	private RecommendationSurvey extractSurvey(ResultSet results) throws SQLException {
		User user = new User(results.getInt("user_id"), results.getString("username"),
			results.getString("email"));
		return new RecommendationSurvey(results.getInt("survey_id"), user,
			results.getString("preferred_genre"), results.getString("preferred_platform"),
			results.getString("play_style"), results.getTimestamp("updated_at"));
	}

	private Game extractGame(ResultSet results) throws SQLException {
		return new Game(results.getInt("app_id"), results.getString("name"),
			results.getDate("release_date"), results.getBigDecimal("price"),
			results.getString("header_image"), nullableInt(results, "metacritic_score"),
			nullableInt(results, "positive_ratings"), nullableInt(results, "negative_ratings"),
			nullableBoolean(results, "windows"), nullableBoolean(results, "mac"),
			nullableBoolean(results, "linux"));
	}

	private Integer nullableInt(ResultSet results, String column) throws SQLException {
		int value = results.getInt(column);
		return results.wasNull() ? null : value;
	}

	private Boolean nullableBoolean(ResultSet results, String column) throws SQLException {
		boolean value = results.getBoolean(column);
		return results.wasNull() ? null : value;
	}
}
