package nextquest.dal;

import nextquest.model.Game;
import nextquest.model.SavedGame;
import nextquest.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * DAO for the SavedGame table (a user's saved / wishlisted games).
 * Reads join User and Game so the pages can show the username and game name.
 */
public class SavedGameDao {
	protected ConnectionManager connectionManager;

	private static SavedGameDao instance = null;

	private static final String SELECT_COLUMNS =
		"SELECT s.user_id,s.app_id,s.saved_at,u.username,u.email," +
		"g.name,g.price,g.header_image " +
		"FROM SavedGame s " +
		"INNER JOIN User u ON u.user_id=s.user_id " +
		"INNER JOIN Game g ON g.app_id=s.app_id ";

	protected SavedGameDao() {
		connectionManager = new ConnectionManager();
	}

	public static SavedGameDao getInstance() {
		if (instance == null) {
			instance = new SavedGameDao();
		}
		return instance;
	}

	/** Saves a game for a user. saved_at defaults to now when not set. */
	public SavedGame create(SavedGame savedGame) throws SQLException {
		String insert = "INSERT INTO SavedGame(user_id,app_id,saved_at) VALUES(?,?,?);";
		if (savedGame.getSavedAt() == null) {
			savedGame.setSavedAt(new Date());
		}
		try (Connection connection = connectionManager.getConnection();
				PreparedStatement statement = connection.prepareStatement(insert)) {
			statement.setInt(1, savedGame.getUser().getUserId());
			statement.setInt(2, savedGame.getGame().getAppId());
			statement.setTimestamp(3, new Timestamp(savedGame.getSavedAt().getTime()));
			statement.executeUpdate();
			return savedGame;
		}
	}

	public SavedGame updateSavedAt(SavedGame savedGame, Date newSavedAt) throws SQLException {
		String update = "UPDATE SavedGame SET saved_at=? WHERE user_id=? AND app_id=?;";
		try (Connection connection = connectionManager.getConnection();
				PreparedStatement statement = connection.prepareStatement(update)) {
			statement.setTimestamp(1, new Timestamp(newSavedAt.getTime()));
			statement.setInt(2, savedGame.getUser().getUserId());
			statement.setInt(3, savedGame.getGame().getAppId());
			statement.executeUpdate();
			savedGame.setSavedAt(newSavedAt);
			return savedGame;
		}
	}

	public SavedGame delete(SavedGame savedGame) throws SQLException {
		String delete = "DELETE FROM SavedGame WHERE user_id=? AND app_id=?;";
		try (Connection connection = connectionManager.getConnection();
				PreparedStatement statement = connection.prepareStatement(delete)) {
			statement.setInt(1, savedGame.getUser().getUserId());
			statement.setInt(2, savedGame.getGame().getAppId());
			statement.executeUpdate();
			return null;
		}
	}

	public SavedGame getSavedGame(int userId, int appId) throws SQLException {
		String select = SELECT_COLUMNS + "WHERE s.user_id=? AND s.app_id=?;";
		try (Connection connection = connectionManager.getConnection();
				PreparedStatement statement = connection.prepareStatement(select)) {
			statement.setInt(1, userId);
			statement.setInt(2, appId);
			try (ResultSet results = statement.executeQuery()) {
				if (results.next()) {
					return extractSavedGame(results);
				}
				return null;
			}
		}
	}

	public List<SavedGame> getSavedGamesByUserId(int userId) throws SQLException {
		String select = SELECT_COLUMNS + "WHERE s.user_id=? ORDER BY s.saved_at DESC;";
		return queryList(select, userId);
	}

	public List<SavedGame> getSavedGamesByAppId(int appId) throws SQLException {
		String select = SELECT_COLUMNS + "WHERE s.app_id=? ORDER BY s.saved_at DESC;";
		return queryList(select, appId);
	}

	private List<SavedGame> queryList(String select, int id) throws SQLException {
		List<SavedGame> savedGames = new ArrayList<SavedGame>();
		try (Connection connection = connectionManager.getConnection();
				PreparedStatement statement = connection.prepareStatement(select)) {
			statement.setInt(1, id);
			try (ResultSet results = statement.executeQuery()) {
				while (results.next()) {
					savedGames.add(extractSavedGame(results));
				}
			}
		}
		return savedGames;
	}

	private SavedGame extractSavedGame(ResultSet results) throws SQLException {
		User user = new User(results.getInt("user_id"), results.getString("username"),
			results.getString("email"));
		Game game = new Game(results.getInt("app_id"));
		game.setName(results.getString("name"));
		game.setPrice(results.getBigDecimal("price"));
		game.setHeaderImage(results.getString("header_image"));
		return new SavedGame(user, game, results.getTimestamp("saved_at"));
	}
}
