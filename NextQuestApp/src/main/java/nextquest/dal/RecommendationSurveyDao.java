package nextquest.dal;

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
