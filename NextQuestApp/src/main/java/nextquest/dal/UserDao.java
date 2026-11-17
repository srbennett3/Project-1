package nextquest.dal;

import nextquest.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for the User table.
 */
public class UserDao {
	protected ConnectionManager connectionManager;

	private static UserDao instance = null;

	protected UserDao() {
		connectionManager = new ConnectionManager();
	}

	public static UserDao getInstance() {
		if (instance == null) {
			instance = new UserDao();
		}
		return instance;
	}

	public User create(User user) throws SQLException {
		String insertUser = "INSERT INTO User(username,email) VALUES(?,?);";
		Connection connection = null;
		PreparedStatement insertStmt = null;
		ResultSet resultKey = null;
		try {
			connection = connectionManager.getConnection();
			insertStmt = connection.prepareStatement(insertUser,
				Statement.RETURN_GENERATED_KEYS);
			insertStmt.setString(1, user.getUsername());
			insertStmt.setString(2, user.getEmail());
			insertStmt.executeUpdate();

			resultKey = insertStmt.getGeneratedKeys();
			int userId = -1;
			if (resultKey.next()) {
				userId = resultKey.getInt(1);
			} else {
				throw new SQLException("Unable to retrieve auto-generated key.");
			}
			user.setUserId(userId);
			return user;
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			close(connection, insertStmt, resultKey);
		}
	}

	public User updateEmail(User user, String newEmail) throws SQLException {
		String updateUser = "UPDATE User SET email=? WHERE user_id=?;";
		Connection connection = null;
		PreparedStatement updateStmt = null;
		try {
			connection = connectionManager.getConnection();
			updateStmt = connection.prepareStatement(updateUser);
			updateStmt.setString(1, newEmail);
			updateStmt.setInt(2, user.getUserId());
			updateStmt.executeUpdate();
			user.setEmail(newEmail);
			return user;
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			close(connection, updateStmt, null);
		}
	}

	public User delete(User user) throws SQLException {
		String deleteUser = "DELETE FROM User WHERE user_id=?;";
		Connection connection = null;
		PreparedStatement deleteStmt = null;
		try {
			connection = connectionManager.getConnection();
			deleteStmt = connection.prepareStatement(deleteUser);
			deleteStmt.setInt(1, user.getUserId());
			deleteStmt.executeUpdate();
			return null;
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			close(connection, deleteStmt, null);
		}
	}

	public User getUserById(int userId) throws SQLException {
		String selectUser = "SELECT user_id,username,email FROM User WHERE user_id=?;";
		Connection connection = null;
		PreparedStatement selectStmt = null;
		ResultSet results = null;
		try {
			connection = connectionManager.getConnection();
			selectStmt = connection.prepareStatement(selectUser);
			selectStmt.setInt(1, userId);
			results = selectStmt.executeQuery();

			if (results.next()) {
				int resultId = results.getInt("user_id");
				String username = results.getString("username");
				String email = results.getString("email");
				return new User(resultId, username, email);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			close(connection, selectStmt, results);
		}
		return null;
	}

	public List<User> getAllUsers() throws SQLException {
		List<User> users = new ArrayList<User>();
		String selectUsers = "SELECT user_id,username,email FROM User ORDER BY username;";
		Connection connection = null;
		PreparedStatement selectStmt = null;
		ResultSet results = null;
		try {
			connection = connectionManager.getConnection();
			selectStmt = connection.prepareStatement(selectUsers);
			results = selectStmt.executeQuery();

			while (results.next()) {
				int userId = results.getInt("user_id");
				String username = results.getString("username");
				String email = results.getString("email");
				users.add(new User(userId, username, email));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			close(connection, selectStmt, results);
		}
		return users;
	}

	public List<User> getUsersByUsername(String username) throws SQLException {
		List<User> users = new ArrayList<User>();
		String selectUsers = "SELECT user_id,username,email FROM User WHERE username=?;";
		Connection connection = null;
		PreparedStatement selectStmt = null;
		ResultSet results = null;
		try {
			connection = connectionManager.getConnection();
			selectStmt = connection.prepareStatement(selectUsers);
			selectStmt.setString(1, username);
			results = selectStmt.executeQuery();

			while (results.next()) {
				int userId = results.getInt("user_id");
				String resultUsername = results.getString("username");
				String email = results.getString("email");
				users.add(new User(userId, resultUsername, email));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			close(connection, selectStmt, results);
		}
		return users;
	}

	private void close(Connection connection, PreparedStatement stmt, ResultSet results)
			throws SQLException {
		try {
			if (results != null) {
				results.close();
			}
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		}
	}
}
