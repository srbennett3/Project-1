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
 * DAO for the Review table.
 */
public class ReviewDao {
	protected ConnectionManager connectionManager;

	private static ReviewDao instance = null;

	/**
	 * Store a ConnectionManager. Returns nothing.
	 */
	protected ReviewDao() {
		connectionManager = new ConnectionManager();
	}

	/**
	 * Return the one ReviewDao. Returns a ReviewDao.
	 */
	public static ReviewDao getInstance() {
		if (instance == null) {
			instance = new ReviewDao();
		}
		return instance;
	}

	/**
	 * Insert one Review.
	 * review is a Review. Returns that Review with review_id set. Throws SQLException.
	 */
	public Review create(Review review) throws SQLException {
		String insert =
			"INSERT INTO Review(app_id,user_id,is_recommended,hours_played,review_date) " +
			"VALUES(?,?,?,?,?);";
		try (Connection connection = connectionManager.getConnection();
				PreparedStatement statement = connection.prepareStatement(insert,
					Statement.RETURN_GENERATED_KEYS)) {
			// Bind the review columns and skip review_id.
			statement.setInt(1, review.getGame().getAppId());
			statement.setInt(2, review.getUser().getUserId());
			setNullableBoolean(statement, 3, review.getIsRecommended());
			statement.setBigDecimal(4, review.getHoursPlayed());
			statement.setDate(5, toSqlDate(review.getReviewDate()));
			statement.executeUpdate();

			// Read the new review_id.
			try (ResultSet keys = statement.getGeneratedKeys()) {
				if (!keys.next()) {
					throw new SQLException("Unable to retrieve generated Review id.");
				}
				review.setReviewId(keys.getInt(1));
			}
			return review;
		}
	}

	/**
	 * Update one Review.
	 * review is a Review. Returns that Review. Throws SQLException.
	 */
	public Review update(Review review) throws SQLException {
		String update =
			"UPDATE Review SET app_id=?,user_id=?,is_recommended=?,hours_played=?," +
			"review_date=? WHERE review_id=?;";
		try (Connection connection = connectionManager.getConnection();
				PreparedStatement statement = connection.prepareStatement(update)) {
			// Write every column for this review_id.
			statement.setInt(1, review.getGame().getAppId());
			statement.setInt(2, review.getUser().getUserId());
			setNullableBoolean(statement, 3, review.getIsRecommended());
			statement.setBigDecimal(4, review.getHoursPlayed());
			statement.setDate(5, toSqlDate(review.getReviewDate()));
			statement.setInt(6, review.getReviewId());
			statement.executeUpdate();
			return review;
		}
	}

	/**
	 * Delete one Review.
	 * review is a Review. Returns null. Throws SQLException.
	 */
	public Review delete(Review review) throws SQLException {
		String delete = "DELETE FROM Review WHERE review_id=?;";
		try (Connection connection = connectionManager.getConnection();
				PreparedStatement statement = connection.prepareStatement(delete)) {
			// Delete the row for this review_id.
			statement.setInt(1, review.getReviewId());
			statement.executeUpdate();
			return null;
		}
	}

	/**
	 * Read one Review by id.
	 * reviewId is an int. Returns the Review, or null when no row matches. Throws SQLException.
	 */
	public Review getReviewById(int reviewId) throws SQLException {
		String select =
			"SELECT review_id,app_id,user_id,is_recommended,hours_played,review_date " +
			"FROM Review WHERE review_id=?;";
		try (Connection connection = connectionManager.getConnection();
				PreparedStatement statement = connection.prepareStatement(select)) {
			// Read the review with this review_id.
			statement.setInt(1, reviewId);
			try (ResultSet results = statement.executeQuery()) {
				if (results.next()) {
					return readReview(results);
				}
				return null;
			}
		}
	}

	/**
	 * Read every Review.
	 * Returns a List of Review. Throws SQLException.
	 */
	public List<Review> getAllReviews() throws SQLException {
		List<Review> reviews = new ArrayList<Review>();
		String select =
			"SELECT review_id,app_id,user_id,is_recommended,hours_played,review_date " +
			"FROM Review ORDER BY review_id;";
		try (Connection connection = connectionManager.getConnection();
				PreparedStatement statement = connection.prepareStatement(select);
				ResultSet results = statement.executeQuery()) {
			// Read every review row.
			while (results.next()) {
				reviews.add(readReview(results));
			}
		}
		return reviews;
	}

	/**
	 * Build a Review from the current row.
	 * results is a ResultSet. Returns a Review. Throws SQLException.
	 */
	private Review readReview(ResultSet results) throws SQLException {
		int reviewId = results.getInt("review_id");
		Game game = new Game(results.getInt("app_id"));
		User user = new User(results.getInt("user_id"));
		boolean recommendedValue = results.getBoolean("is_recommended");
		Boolean isRecommended = results.wasNull() ? null : Boolean.valueOf(recommendedValue);
		return new Review(reviewId, game, user, isRecommended,
			results.getBigDecimal("hours_played"), results.getDate("review_date"));
	}

	/**
	 * Turn a Date into a SQL date.
	 * date is a Date. Returns a java.sql.Date, or null when date is null.
	 */
	private java.sql.Date toSqlDate(java.util.Date date) {
		if (date == null) {
			return null;
		}
		return new java.sql.Date(date.getTime());
	}

	/**
	 * Bind a Boolean that may be null.
	 * statement is a PreparedStatement, index is an int, value is a Boolean.
	 * Returns nothing. Throws SQLException.
	 */
	private void setNullableBoolean(PreparedStatement statement, int index, Boolean value)
			throws SQLException {
		if (value == null) {
			statement.setNull(index, java.sql.Types.BOOLEAN);
		} else {
			statement.setBoolean(index, value);
		}
	}
}
