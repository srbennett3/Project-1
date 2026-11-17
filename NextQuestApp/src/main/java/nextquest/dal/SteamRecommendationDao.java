package nextquest.dal;

import nextquest.model.Game;
import nextquest.model.SteamRecommendation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for the SteamRecommendation table.
 */
public class SteamRecommendationDao {
	protected ConnectionManager connectionManager;

	private static SteamRecommendationDao instance = null;

	protected SteamRecommendationDao() {
		connectionManager = new ConnectionManager();
	}

	public static SteamRecommendationDao getInstance() {
		if (instance == null) {
			instance = new SteamRecommendationDao();
		}
		return instance;
	}

	public SteamRecommendation create(SteamRecommendation recommendation) throws SQLException {
		String insert =
			"INSERT INTO SteamRecommendation(app_id,author_id,is_recommended," +
			"hours_played,recommendation_date) VALUES(?,?,?,?,?);";
		try (Connection connection = connectionManager.getConnection();
				PreparedStatement statement = connection.prepareStatement(insert,
					Statement.RETURN_GENERATED_KEYS)) {
			statement.setInt(1, recommendation.getGame().getAppId());
			setNullableInt(statement, 2, recommendation.getAuthorId());
			setNullableBoolean(statement, 3, recommendation.getIsRecommended());
			statement.setBigDecimal(4, recommendation.getHoursPlayed());
			statement.setDate(5, toSqlDate(recommendation.getRecommendationDate()));
			statement.executeUpdate();

			try (ResultSet keys = statement.getGeneratedKeys()) {
				if (!keys.next()) {
					throw new SQLException("Unable to retrieve generated SteamRecommendation id.");
				}
				recommendation.setSteamRecommendationId(keys.getInt(1));
			}
			return recommendation;
		}
	}

	public SteamRecommendation update(SteamRecommendation recommendation) throws SQLException {
		String update =
			"UPDATE SteamRecommendation SET app_id=?,author_id=?,is_recommended=?," +
			"hours_played=?,recommendation_date=? WHERE steam_recommendation_id=?;";
		try (Connection connection = connectionManager.getConnection();
				PreparedStatement statement = connection.prepareStatement(update)) {
			statement.setInt(1, recommendation.getGame().getAppId());
			setNullableInt(statement, 2, recommendation.getAuthorId());
			setNullableBoolean(statement, 3, recommendation.getIsRecommended());
			statement.setBigDecimal(4, recommendation.getHoursPlayed());
			statement.setDate(5, toSqlDate(recommendation.getRecommendationDate()));
			statement.setInt(6, recommendation.getSteamRecommendationId());
			statement.executeUpdate();
			return recommendation;
		}
	}

	public SteamRecommendation delete(SteamRecommendation recommendation) throws SQLException {
		String delete = "DELETE FROM SteamRecommendation WHERE steam_recommendation_id=?;";
		try (Connection connection = connectionManager.getConnection();
				PreparedStatement statement = connection.prepareStatement(delete)) {
			statement.setInt(1, recommendation.getSteamRecommendationId());
			statement.executeUpdate();
			return null;
		}
	}

	public SteamRecommendation getSteamRecommendationById(int id) throws SQLException {
		String select =
			"SELECT steam_recommendation_id,app_id,author_id,is_recommended," +
			"hours_played,recommendation_date FROM SteamRecommendation " +
			"WHERE steam_recommendation_id=?;";
		try (Connection connection = connectionManager.getConnection();
				PreparedStatement statement = connection.prepareStatement(select)) {
			statement.setInt(1, id);
			try (ResultSet results = statement.executeQuery()) {
				if (results.next()) {
					return extractRecommendation(results);
				}
				return null;
			}
		}
	}

	public List<SteamRecommendation> getAllSteamRecommendations() throws SQLException {
		List<SteamRecommendation> recommendations = new ArrayList<SteamRecommendation>();
		String select =
			"SELECT steam_recommendation_id,app_id,author_id,is_recommended," +
			"hours_played,recommendation_date FROM SteamRecommendation " +
			"ORDER BY steam_recommendation_id;";
		try (Connection connection = connectionManager.getConnection();
				PreparedStatement statement = connection.prepareStatement(select);
				ResultSet results = statement.executeQuery()) {
			while (results.next()) {
				recommendations.add(extractRecommendation(results));
			}
		}
		return recommendations;
	}

	public List<SteamRecommendation> getSteamRecommendations(int offset, int limit)
			throws SQLException {
		List<SteamRecommendation> recommendations = new ArrayList<SteamRecommendation>();
		String select =
			"SELECT steam_recommendation_id,app_id,author_id,is_recommended," +
			"hours_played,recommendation_date FROM SteamRecommendation " +
			"ORDER BY steam_recommendation_id LIMIT ? OFFSET ?;";
		try (Connection connection = connectionManager.getConnection();
				PreparedStatement statement = connection.prepareStatement(select)) {
			statement.setInt(1, limit);
			statement.setInt(2, offset);
			try (ResultSet results = statement.executeQuery()) {
				while (results.next()) {
					recommendations.add(extractRecommendation(results));
				}
			}
		}
		return recommendations;
	}

	public int getSteamRecommendationCount() throws SQLException {
		String select = "SELECT COUNT(*) FROM SteamRecommendation;";
		try (Connection connection = connectionManager.getConnection();
				PreparedStatement statement = connection.prepareStatement(select);
				ResultSet results = statement.executeQuery()) {
			results.next();
			return results.getInt(1);
		}
	}

	public List<SteamRecommendation> getSteamRecommendationsByAppId(
			int appId, int offset, int limit) throws SQLException {
		List<SteamRecommendation> recommendations = new ArrayList<SteamRecommendation>();
		String select =
			"SELECT steam_recommendation_id,app_id,author_id,is_recommended," +
			"hours_played,recommendation_date FROM SteamRecommendation WHERE app_id=? " +
			"ORDER BY recommendation_date DESC,steam_recommendation_id DESC LIMIT ? OFFSET ?;";
		try (Connection connection = connectionManager.getConnection();
				PreparedStatement statement = connection.prepareStatement(select)) {
			statement.setInt(1, appId);
			statement.setInt(2, limit);
			statement.setInt(3, offset);
			try (ResultSet results = statement.executeQuery()) {
				while (results.next()) {
					recommendations.add(extractRecommendation(results));
				}
			}
		}
		return recommendations;
	}

	public int getSteamRecommendationCountByAppId(int appId) throws SQLException {
		String select = "SELECT COUNT(*) FROM SteamRecommendation WHERE app_id=?;";
		try (Connection connection = connectionManager.getConnection();
				PreparedStatement statement = connection.prepareStatement(select)) {
			statement.setInt(1, appId);
			try (ResultSet results = statement.executeQuery()) {
				results.next();
				return results.getInt(1);
			}
		}
	}

	private SteamRecommendation extractRecommendation(ResultSet results) throws SQLException {
		int id = results.getInt("steam_recommendation_id");
		Game game = new Game(results.getInt("app_id"));
		int authorIdValue = results.getInt("author_id");
		Integer authorId = results.wasNull() ? null : authorIdValue;
		boolean recommendedValue = results.getBoolean("is_recommended");
		Boolean recommended = results.wasNull() ? null : recommendedValue;
		return new SteamRecommendation(id, game, authorId, recommended,
			results.getBigDecimal("hours_played"), results.getDate("recommendation_date"));
	}

	private java.sql.Date toSqlDate(java.util.Date date) {
		return date == null ? null : new java.sql.Date(date.getTime());
	}

	private void setNullableInt(PreparedStatement statement, int index, Integer value)
			throws SQLException {
		if (value == null) {
			statement.setNull(index, java.sql.Types.INTEGER);
		} else {
			statement.setInt(index, value);
		}
	}

	private void setNullableBoolean(PreparedStatement statement, int index, Boolean value)
			throws SQLException {
		if (value == null) {
			statement.setNull(index, java.sql.Types.BOOLEAN);
		} else {
			statement.setBoolean(index, value);
		}
	}
}
