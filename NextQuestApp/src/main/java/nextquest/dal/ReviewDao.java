package nextquest.dal;

import nextquest.model.Game;
import nextquest.model.Review;
import nextquest.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for the Review table (reviews written by NextQuest users).
 * (user_id, app_id) is unique, so creating a second review of the same game
 * by the same user throws SQLIntegrityConstraintViolationException.
 */
public class ReviewDao {
	protected ConnectionManager connectionManager;

	private static ReviewDao instance = null;

	private static final String SELECT_COLUMNS =
		"SELECT r.review_id,r.app_id,r.user_id,r.is_recommended,r.hours_played," +
		"r.review_date,u.username,u.email,g.name " +
		"FROM Review r " +
		"INNER JOIN User u ON u.user_id=r.user_id " +
		"INNER JOIN Game g ON g.app_id=r.app_id ";

	protected ReviewDao() {
		connectionManager = new ConnectionManager();
	}

	public static ReviewDao getInstance() {
		if (instance == null) {
			instance = new ReviewDao();
		}
		return instance;
	}

	public Review create(Review review) throws SQLException {
		String insert =
			"INSERT INTO Review(app_id,user_id,is_recommended,hours_played,review_date) " +
			"VALUES(?,?,?,?,?);";
		try (Connection connection = connectionManager.getConnection();
				PreparedStatement statement = connection.prepareStatement(insert,
					Statement.RETURN_GENERATED_KEYS)) {
			statement.setInt(1, review.getGame().getAppId());
			statement.setInt(2, review.getUser().getUserId());
			setNullableBoolean(statement, 3, review.getIsRecommended());
			statement.setBigDecimal(4, review.getHoursPlayed());
			statement.setDate(5, toSqlDate(review.getReviewDate()));
			statement.executeUpdate();

			try (ResultSet keys = statement.getGeneratedKeys()) {
				if (!keys.next()) {
					throw new SQLException("Unable to retrieve generated Review id.");
				}
				review.setReviewId(keys.getInt(1));
			}
			return review;
		}
	}

	/** Updates the reviewer's verdict, hours played, and date. Game and user stay fixed. */
	public Review update(Review review) throws SQLException {
		String update =
			"UPDATE Review SET is_recommended=?,hours_played=?,review_date=? WHERE review_id=?;";
		try (Connection connection = connectionManager.getConnection();
				PreparedStatement statement = connection.prepareStatement(update)) {
			setNullableBoolean(statement, 1, review.getIsRecommended());
			statement.setBigDecimal(2, review.getHoursPlayed());
			statement.setDate(3, toSqlDate(review.getReviewDate()));
			statement.setInt(4, review.getReviewId());
			statement.executeUpdate();
			return review;
		}
	}

	public Review delete(Review review) throws SQLException {
		String delete = "DELETE FROM Review WHERE review_id=?;";
		try (Connection connection = connectionManager.getConnection();
				PreparedStatement statement = connection.prepareStatement(delete)) {
			statement.setInt(1, review.getReviewId());
			statement.executeUpdate();
			return null;
		}
	}

	public Review getReviewById(int reviewId) throws SQLException {
		List<Review> reviews = queryList(SELECT_COLUMNS + "WHERE r.review_id=?;", reviewId);
		return reviews.isEmpty() ? null : reviews.get(0);
	}

	public Review getReviewByUserIdAndAppId(int userId, int appId) throws SQLException {
		String select = SELECT_COLUMNS + "WHERE r.user_id=? AND r.app_id=?;";
		try (Connection connection = connectionManager.getConnection();
				PreparedStatement statement = connection.prepareStatement(select)) {
			statement.setInt(1, userId);
			statement.setInt(2, appId);
			try (ResultSet results = statement.executeQuery()) {
				if (results.next()) {
					return extractReview(results);
				}
				return null;
			}
		}
	}

	public List<Review> getReviewsByUserId(int userId) throws SQLException {
		return queryList(SELECT_COLUMNS +
			"WHERE r.user_id=? ORDER BY r.review_date DESC,r.review_id DESC;", userId);
	}

	public List<Review> getReviewsByAppId(int appId) throws SQLException {
		return queryList(SELECT_COLUMNS +
			"WHERE r.app_id=? ORDER BY r.review_date DESC,r.review_id DESC;", appId);
	}

	public List<Review> getAllReviews() throws SQLException {
		List<Review> reviews = new ArrayList<Review>();
		String select = SELECT_COLUMNS + "ORDER BY r.review_date DESC,r.review_id DESC;";
		try (Connection connection = connectionManager.getConnection();
				PreparedStatement statement = connection.prepareStatement(select);
				ResultSet results = statement.executeQuery()) {
			while (results.next()) {
				reviews.add(extractReview(results));
			}
		}
		return reviews;
	}

	private List<Review> queryList(String select, int id) throws SQLException {
		List<Review> reviews = new ArrayList<Review>();
		try (Connection connection = connectionManager.getConnection();
				PreparedStatement statement = connection.prepareStatement(select)) {
			statement.setInt(1, id);
			try (ResultSet results = statement.executeQuery()) {
				while (results.next()) {
					reviews.add(extractReview(results));
				}
			}
		}
		return reviews;
	}

	private Review extractReview(ResultSet results) throws SQLException {
		Game game = new Game(results.getInt("app_id"));
		game.setName(results.getString("name"));
		User user = new User(results.getInt("user_id"), results.getString("username"),
			results.getString("email"));
		boolean recommendedValue = results.getBoolean("is_recommended");
		Boolean recommended = results.wasNull() ? null : recommendedValue;
		return new Review(results.getInt("review_id"), game, user, recommended,
			results.getBigDecimal("hours_played"), results.getDate("review_date"));
	}

	private java.sql.Date toSqlDate(java.util.Date date) {
		return date == null ? null : new java.sql.Date(date.getTime());
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
