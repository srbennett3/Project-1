package nextquest.dal;

import nextquest.model.Game;
import nextquest.model.SteamReview;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for the SteamReview table.
 */
public class SteamReviewDao {
	protected ConnectionManager connectionManager;

	private static SteamReviewDao instance = null;

	protected SteamReviewDao() {
		connectionManager = new ConnectionManager();
	}

	public static SteamReviewDao getInstance() {
		if (instance == null) {
			instance = new SteamReviewDao();
		}
		return instance;
	}

	public SteamReview create(SteamReview review) throws SQLException {
		String insert =
			"INSERT INTO SteamReview(app_id,author_steamid,is_recommended," +
			"hours_played,review_date,language) VALUES(?,?,?,?,?,?);";
		try (Connection connection = connectionManager.getConnection();
				PreparedStatement statement = connection.prepareStatement(insert,
					Statement.RETURN_GENERATED_KEYS)) {
			statement.setInt(1, review.getGame().getAppId());
			setNullableLong(statement, 2, review.getAuthorSteamId());
			setNullableBoolean(statement, 3, review.getIsRecommended());
			statement.setBigDecimal(4, review.getHoursPlayed());
			statement.setDate(5, toSqlDate(review.getReviewDate()));
			statement.setString(6, review.getLanguage());
			statement.executeUpdate();

			try (ResultSet keys = statement.getGeneratedKeys()) {
				if (!keys.next()) {
					throw new SQLException("Unable to retrieve generated SteamReview id.");
				}
				review.setSteamReviewId(keys.getInt(1));
			}
			return review;
		}
	}

	public SteamReview update(SteamReview review) throws SQLException {
		String update =
			"UPDATE SteamReview SET app_id=?,author_steamid=?,is_recommended=?," +
			"hours_played=?,review_date=?,language=? WHERE steam_review_id=?;";
		try (Connection connection = connectionManager.getConnection();
				PreparedStatement statement = connection.prepareStatement(update)) {
			statement.setInt(1, review.getGame().getAppId());
			setNullableLong(statement, 2, review.getAuthorSteamId());
			setNullableBoolean(statement, 3, review.getIsRecommended());
			statement.setBigDecimal(4, review.getHoursPlayed());
			statement.setDate(5, toSqlDate(review.getReviewDate()));
			statement.setString(6, review.getLanguage());
			statement.setInt(7, review.getSteamReviewId());
			statement.executeUpdate();
			return review;
		}
	}

	public SteamReview delete(SteamReview review) throws SQLException {
		String delete = "DELETE FROM SteamReview WHERE steam_review_id=?;";
		try (Connection connection = connectionManager.getConnection();
				PreparedStatement statement = connection.prepareStatement(delete)) {
			statement.setInt(1, review.getSteamReviewId());
			statement.executeUpdate();
			return null;
		}
	}

	public SteamReview getSteamReviewById(int id) throws SQLException {
		String select =
			"SELECT steam_review_id,app_id,author_steamid,is_recommended," +
			"hours_played,review_date,language FROM SteamReview WHERE steam_review_id=?;";
		try (Connection connection = connectionManager.getConnection();
				PreparedStatement statement = connection.prepareStatement(select)) {
			statement.setInt(1, id);
			try (ResultSet results = statement.executeQuery()) {
				if (results.next()) {
					return extractReview(results);
				}
				return null;
			}
		}
	}

	public List<SteamReview> getAllSteamReviews() throws SQLException {
		List<SteamReview> reviews = new ArrayList<SteamReview>();
		String select =
			"SELECT steam_review_id,app_id,author_steamid,is_recommended," +
			"hours_played,review_date,language FROM SteamReview ORDER BY steam_review_id;";
		try (Connection connection = connectionManager.getConnection();
				PreparedStatement statement = connection.prepareStatement(select);
				ResultSet results = statement.executeQuery()) {
			while (results.next()) {
				reviews.add(extractReview(results));
			}
		}
		return reviews;
	}

	public List<SteamReview> getSteamReviews(int offset, int limit) throws SQLException {
		List<SteamReview> reviews = new ArrayList<SteamReview>();
		String select =
			"SELECT steam_review_id,app_id,author_steamid,is_recommended," +
			"hours_played,review_date,language FROM SteamReview " +
			"ORDER BY steam_review_id LIMIT ? OFFSET ?;";
		try (Connection connection = connectionManager.getConnection();
				PreparedStatement statement = connection.prepareStatement(select)) {
			statement.setInt(1, limit);
			statement.setInt(2, offset);
			try (ResultSet results = statement.executeQuery()) {
				while (results.next()) {
					reviews.add(extractReview(results));
				}
			}
		}
		return reviews;
	}

	public int getSteamReviewCount() throws SQLException {
		String select = "SELECT COUNT(*) FROM SteamReview;";
		try (Connection connection = connectionManager.getConnection();
				PreparedStatement statement = connection.prepareStatement(select);
				ResultSet results = statement.executeQuery()) {
			results.next();
			return results.getInt(1);
		}
	}

	public List<SteamReview> getSteamReviewsByAppId(int appId, int offset, int limit)
			throws SQLException {
		List<SteamReview> reviews = new ArrayList<SteamReview>();
		String select =
			"SELECT steam_review_id,app_id,author_steamid,is_recommended," +
			"hours_played,review_date,language FROM SteamReview WHERE app_id=? " +
			"ORDER BY review_date DESC,steam_review_id DESC LIMIT ? OFFSET ?;";
		try (Connection connection = connectionManager.getConnection();
				PreparedStatement statement = connection.prepareStatement(select)) {
			statement.setInt(1, appId);
			statement.setInt(2, limit);
			statement.setInt(3, offset);
			try (ResultSet results = statement.executeQuery()) {
				while (results.next()) {
					reviews.add(extractReview(results));
				}
			}
		}
		return reviews;
	}

	public int getSteamReviewCountByAppId(int appId) throws SQLException {
		String select = "SELECT COUNT(*) FROM SteamReview WHERE app_id=?;";
		try (Connection connection = connectionManager.getConnection();
				PreparedStatement statement = connection.prepareStatement(select)) {
			statement.setInt(1, appId);
			try (ResultSet results = statement.executeQuery()) {
				results.next();
				return results.getInt(1);
			}
		}
	}

	private SteamReview extractReview(ResultSet results) throws SQLException {
		int id = results.getInt("steam_review_id");
		Game game = new Game(results.getInt("app_id"));
		long authorSteamIdValue = results.getLong("author_steamid");
		Long authorSteamId = results.wasNull() ? null : authorSteamIdValue;
		boolean recommendedValue = results.getBoolean("is_recommended");
		Boolean recommended = results.wasNull() ? null : recommendedValue;
		return new SteamReview(id, game, authorSteamId, recommended,
			results.getBigDecimal("hours_played"), results.getDate("review_date"),
			results.getString("language"));
	}

	private java.sql.Date toSqlDate(java.util.Date date) {
		return date == null ? null : new java.sql.Date(date.getTime());
	}

	private void setNullableLong(PreparedStatement statement, int index, Long value)
			throws SQLException {
		if (value == null) {
			statement.setNull(index, java.sql.Types.BIGINT);
		} else {
			statement.setLong(index, value);
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
