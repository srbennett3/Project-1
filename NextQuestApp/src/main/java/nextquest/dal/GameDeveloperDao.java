package nextquest.dal;

import nextquest.model.Developer;
import nextquest.model.Game;
import nextquest.model.GameDeveloper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for the GameDeveloper junction table.
 * Reads join Game and Developer so each row carries the game name and developer name.
 */
public class GameDeveloperDao {
	protected ConnectionManager connectionManager;

	private static GameDeveloperDao instance = null;

	private static final String SELECT_COLUMNS =
		"SELECT gd.app_id,gd.developer_id,g.name AS game_name,d.name AS developer_name " +
		"FROM GameDeveloper gd " +
		"INNER JOIN Game g ON g.app_id=gd.app_id " +
		"INNER JOIN Developer d ON d.developer_id=gd.developer_id ";

	protected GameDeveloperDao() {
		connectionManager = new ConnectionManager();
	}

	public static GameDeveloperDao getInstance() {
		if (instance == null) {
			instance = new GameDeveloperDao();
		}
		return instance;
	}

	public GameDeveloper create(GameDeveloper gameDeveloper) throws SQLException {
		String insert = "INSERT INTO GameDeveloper(app_id,developer_id) VALUES(?,?);";
		try (Connection connection = connectionManager.getConnection();
				PreparedStatement statement = connection.prepareStatement(insert)) {
			statement.setInt(1, gameDeveloper.getGame().getAppId());
			statement.setInt(2, gameDeveloper.getDeveloper().getDeveloperId());
			statement.executeUpdate();
			return gameDeveloper;
		}
	}

	/** Credits a game to a different developer (the pair is the primary key). */
	public GameDeveloper updateDeveloper(GameDeveloper gameDeveloper, Developer newDeveloper)
			throws SQLException {
		String update =
			"UPDATE GameDeveloper SET developer_id=? WHERE app_id=? AND developer_id=?;";
		try (Connection connection = connectionManager.getConnection();
				PreparedStatement statement = connection.prepareStatement(update)) {
			statement.setInt(1, newDeveloper.getDeveloperId());
			statement.setInt(2, gameDeveloper.getGame().getAppId());
			statement.setInt(3, gameDeveloper.getDeveloper().getDeveloperId());
			statement.executeUpdate();
			gameDeveloper.setDeveloper(newDeveloper);
			return gameDeveloper;
		}
	}

	public GameDeveloper delete(GameDeveloper gameDeveloper) throws SQLException {
		String delete = "DELETE FROM GameDeveloper WHERE app_id=? AND developer_id=?;";
		try (Connection connection = connectionManager.getConnection();
				PreparedStatement statement = connection.prepareStatement(delete)) {
			statement.setInt(1, gameDeveloper.getGame().getAppId());
			statement.setInt(2, gameDeveloper.getDeveloper().getDeveloperId());
			statement.executeUpdate();
			return null;
		}
	}

	public GameDeveloper getGameDeveloper(int appId, int developerId) throws SQLException {
		String select = SELECT_COLUMNS + "WHERE gd.app_id=? AND gd.developer_id=?;";
		try (Connection connection = connectionManager.getConnection();
				PreparedStatement statement = connection.prepareStatement(select)) {
			statement.setInt(1, appId);
			statement.setInt(2, developerId);
			try (ResultSet results = statement.executeQuery()) {
				if (results.next()) {
					return extractGameDeveloper(results);
				}
				return null;
			}
		}
	}

	/** All developers of one game, by developer name. */
	public List<GameDeveloper> getGameDevelopersByAppId(int appId) throws SQLException {
		List<GameDeveloper> gameDevelopers = new ArrayList<GameDeveloper>();
		String select = SELECT_COLUMNS + "WHERE gd.app_id=? ORDER BY d.name;";
		try (Connection connection = connectionManager.getConnection();
				PreparedStatement statement = connection.prepareStatement(select)) {
			statement.setInt(1, appId);
			try (ResultSet results = statement.executeQuery()) {
				while (results.next()) {
					gameDevelopers.add(extractGameDeveloper(results));
				}
			}
		}
		return gameDevelopers;
	}

	/** One page of the games made by a developer, by game name. */
	public List<GameDeveloper> getGameDevelopersByDeveloperId(int developerId, int offset,
			int limit) throws SQLException {
		List<GameDeveloper> gameDevelopers = new ArrayList<GameDeveloper>();
		String select = SELECT_COLUMNS +
			"WHERE gd.developer_id=? ORDER BY g.name,g.app_id LIMIT ? OFFSET ?;";
		try (Connection connection = connectionManager.getConnection();
				PreparedStatement statement = connection.prepareStatement(select)) {
			statement.setInt(1, developerId);
			statement.setInt(2, limit);
			statement.setInt(3, offset);
			try (ResultSet results = statement.executeQuery()) {
				while (results.next()) {
					gameDevelopers.add(extractGameDeveloper(results));
				}
			}
		}
		return gameDevelopers;
	}

	public int getGameCountByDeveloperId(int developerId) throws SQLException {
		String select = "SELECT COUNT(*) FROM GameDeveloper WHERE developer_id=?;";
		try (Connection connection = connectionManager.getConnection();
				PreparedStatement statement = connection.prepareStatement(select)) {
			statement.setInt(1, developerId);
			try (ResultSet results = statement.executeQuery()) {
				results.next();
				return results.getInt(1);
			}
		}
	}

	private GameDeveloper extractGameDeveloper(ResultSet results) throws SQLException {
		Game game = new Game(results.getInt("app_id"));
		game.setName(results.getString("game_name"));
		Developer developer = new Developer(results.getInt("developer_id"),
			results.getString("developer_name"));
		return new GameDeveloper(game, developer);
	}
}
