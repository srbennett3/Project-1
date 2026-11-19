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
import java.util.List;

/**
 * DAO for the RecommendationSurvey table.
 */
public class RecommendationSurveyDao {
	protected ConnectionManager connectionManager;

	private static RecommendationSurveyDao instance = null;

	/** Platforms the survey form offers. Each one is also a BOOLEAN column on Game. */
	public static final String[] PLATFORMS = {"windows", "mac", "linux"};

	/** Play styles the survey form offers: free = price 0, budget = under $10,
	 * acclaimed = Metacritic 80+, any = no filter. */
	public static final String[] PLAY_STYLES = {"any", "free", "budget", "acclaimed"};

	/** A game needs this many Steam ratings before it can be recommended. */
	private static final int MIN_RATINGS = 50;

	/**
	 * Store a ConnectionManager. Returns nothing.
	 */
	protected RecommendationSurveyDao() {
		connectionManager = new ConnectionManager();
	}

	/**
	 * Return the one RecommendationSurveyDao. Returns a RecommendationSurveyDao.
	 */
	public static RecommendationSurveyDao getInstance() {
		if (instance == null) {
			instance = new RecommendationSurveyDao();
		}
		return instance;
	}

	/**
	 * Insert one RecommendationSurvey.
	 * survey is a RecommendationSurvey. Returns that survey with survey_id and updated_at set.
	 * Throws SQLException.
	 */
	public RecommendationSurvey create(RecommendationSurvey survey) throws SQLException {
		String insert =
			"INSERT INTO RecommendationSurvey(user_id,preferred_genre,preferred_platform," +
			"play_style,updated_at) VALUES(?,?,?,?,?);";
		try (Connection connection = connectionManager.getConnection();
				PreparedStatement statement = connection.prepareStatement(insert,
					Statement.RETURN_GENERATED_KEYS)) {
			// Bind the survey columns and skip survey_id.
			statement.setInt(1, survey.getUser().getUserId());
			statement.setString(2, survey.getPreferredGenre());
			statement.setString(3, survey.getPreferredPlatform());
			statement.setString(4, survey.getPlayStyle());
			// Set updated_at to the current time.
			Timestamp updatedAt = new Timestamp(System.currentTimeMillis());
			statement.setTimestamp(5, updatedAt);
			statement.executeUpdate();

			// Read the new survey_id.
			try (ResultSet keys = statement.getGeneratedKeys()) {
				if (!keys.next()) {
					throw new SQLException("Unable to retrieve generated RecommendationSurvey id.");
				}
				survey.setSurveyId(keys.getInt(1));
			}
			survey.setUpdatedAt(updatedAt);
			return survey;
		}
	}

	/**
	 * Update one RecommendationSurvey.
	 * survey is a RecommendationSurvey. Returns that survey with updated_at set.
	 * Throws SQLException.
	 */
	public RecommendationSurvey update(RecommendationSurvey survey) throws SQLException {
		String update =
			"UPDATE RecommendationSurvey SET user_id=?,preferred_genre=?,preferred_platform=?," +
			"play_style=?,updated_at=? WHERE survey_id=?;";
		try (Connection connection = connectionManager.getConnection();
				PreparedStatement statement = connection.prepareStatement(update)) {
			// Write every column for this survey_id.
			statement.setInt(1, survey.getUser().getUserId());
			statement.setString(2, survey.getPreferredGenre());
			statement.setString(3, survey.getPreferredPlatform());
			statement.setString(4, survey.getPlayStyle());
			// Set updated_at to the current time.
			Timestamp updatedAt = new Timestamp(System.currentTimeMillis());
			statement.setTimestamp(5, updatedAt);
			statement.setInt(6, survey.getSurveyId());
			statement.executeUpdate();
			survey.setUpdatedAt(updatedAt);
			return survey;
		}
	}

	/**
	 * Delete one RecommendationSurvey.
	 * survey is a RecommendationSurvey. Returns null. Throws SQLException.
	 */
	public RecommendationSurvey delete(RecommendationSurvey survey) throws SQLException {
		String delete = "DELETE FROM RecommendationSurvey WHERE survey_id=?;";
		try (Connection connection = connectionManager.getConnection();
				PreparedStatement statement = connection.prepareStatement(delete)) {
			// Delete the row for this survey_id.
			statement.setInt(1, survey.getSurveyId());
			statement.executeUpdate();
			return null;
		}
	}

	/**
	 * Read one RecommendationSurvey by id.
	 * surveyId is an int. Returns the survey, or null when no row matches. Throws SQLException.
	 */
	public RecommendationSurvey getRecommendationSurveyById(int surveyId) throws SQLException {
		String select =
			"SELECT survey_id,user_id,preferred_genre,preferred_platform,play_style,updated_at " +
			"FROM RecommendationSurvey WHERE survey_id=?;";
		try (Connection connection = connectionManager.getConnection();
				PreparedStatement statement = connection.prepareStatement(select)) {
			// Read the survey with this survey_id.
			statement.setInt(1, surveyId);
			try (ResultSet results = statement.executeQuery()) {
				if (results.next()) {
					return readSurvey(results);
				}
				return null;
			}
		}
	}

	/**
	 * Read every RecommendationSurvey.
	 * Returns a List of RecommendationSurvey. Throws SQLException.
	 */
	public List<RecommendationSurvey> getAllRecommendationSurveys() throws SQLException {
		List<RecommendationSurvey> surveys = new ArrayList<RecommendationSurvey>();
		String select =
			"SELECT survey_id,user_id,preferred_genre,preferred_platform,play_style,updated_at " +
			"FROM RecommendationSurvey ORDER BY survey_id;";
		try (Connection connection = connectionManager.getConnection();
				PreparedStatement statement = connection.prepareStatement(select);
				ResultSet results = statement.executeQuery()) {
			// Read every survey row.
			while (results.next()) {
				surveys.add(readSurvey(results));
			}
		}
		return surveys;
	}

	/**
	 * Read every RecommendationSurvey of one user.
	 * userId is an int. Returns a List of RecommendationSurvey. Throws SQLException.
	 */
	public List<RecommendationSurvey> getRecommendationSurveysByUserId(int userId)
			throws SQLException {
		List<RecommendationSurvey> surveys = new ArrayList<RecommendationSurvey>();
		String select =
			"SELECT survey_id,user_id,preferred_genre,preferred_platform,play_style,updated_at " +
			"FROM RecommendationSurvey WHERE user_id=? ORDER BY survey_id;";
		try (Connection connection = connectionManager.getConnection();
				PreparedStatement statement = connection.prepareStatement(select)) {
			// Read the surveys that belong to this user_id.
			statement.setInt(1, userId);
			try (ResultSet results = statement.executeQuery()) {
				while (results.next()) {
					surveys.add(readSurvey(results));
				}
			}
		}
		return surveys;
	}

	/**
	 * Recommend games for one survey: games in the preferred genre, on the preferred
	 * platform, matching the play style, with at least MIN_RATINGS Steam ratings, that
	 * the user has not saved or reviewed yet. Best share of positive ratings first.
	 * A blank or unknown genre, platform, or play style does not filter.
	 * survey is a RecommendationSurvey. limit is an int. Returns a List of Game.
	 * Throws SQLException.
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

		// The platform becomes a column name, so it is only used when it is on the list.
		String platform = survey.getPreferredPlatform();
		for (String allowed : PLATFORMS) {
			if (allowed.equalsIgnoreCase(platform == null ? "" : platform.trim())) {
				select.append("AND g.").append(allowed).append("=TRUE ");
			}
		}

		String playStyle = survey.getPlayStyle() == null ? "" : survey.getPlayStyle().trim();
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
			// Bind the filters in the order they were added.
			for (int i = 0; i < params.size(); i++) {
				statement.setObject(i + 1, params.get(i));
			}
			try (ResultSet results = statement.executeQuery()) {
				while (results.next()) {
					games.add(readGame(results));
				}
			}
		}
		return games;
	}

	/**
	 * Build a Game from the current row of getRecommendedGames.
	 * results is a ResultSet. Returns a Game. Throws SQLException.
	 */
	private Game readGame(ResultSet results) throws SQLException {
		return new Game(results.getInt("app_id"), results.getString("name"),
			results.getDate("release_date"), results.getBigDecimal("price"),
			results.getString("header_image"), readInteger(results, "metacritic_score"),
			readInteger(results, "positive_ratings"), readInteger(results, "negative_ratings"),
			readBoolean(results, "windows"), readBoolean(results, "mac"),
			readBoolean(results, "linux"));
	}

	/**
	 * Read a nullable INT column. Returns an Integer, or null for SQL NULL.
	 */
	private Integer readInteger(ResultSet results, String column) throws SQLException {
		int value = results.getInt(column);
		return results.wasNull() ? null : value;
	}

	/**
	 * Read a nullable BOOLEAN column. Returns a Boolean, or null for SQL NULL.
	 */
	private Boolean readBoolean(ResultSet results, String column) throws SQLException {
		boolean value = results.getBoolean(column);
		return results.wasNull() ? null : value;
	}

	/**
	 * Build a RecommendationSurvey from the current row.
	 * results is a ResultSet. Returns a RecommendationSurvey. Throws SQLException.
	 */
	private RecommendationSurvey readSurvey(ResultSet results) throws SQLException {
		int surveyId = results.getInt("survey_id");
		User user = new User(results.getInt("user_id"));
		return new RecommendationSurvey(surveyId, user,
			results.getString("preferred_genre"),
			results.getString("preferred_platform"),
			results.getString("play_style"),
			results.getTimestamp("updated_at"));
	}
}
