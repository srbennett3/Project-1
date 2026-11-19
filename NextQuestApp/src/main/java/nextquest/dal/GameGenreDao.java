package nextquest.dal;

import nextquest.model.Game;
import nextquest.model.GameGenre;
import nextquest.model.Genre;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for the GameGenre junction table.
 * Reads join Game and Genre so each row carries the game name and genre name.
 */
public class GameGenreDao {
	protected ConnectionManager connectionManager;

	private static GameGenreDao instance = null;

	private static final String SELECT_COLUMNS =
		"SELECT gg.app_id,gg.genre_id,g.name AS game_name,ge.name AS genre_name " +
		"FROM GameGenre gg " +
		"INNER JOIN Game g ON g.app_id=gg.app_id " +
		"INNER JOIN Genre ge ON ge.genre_id=gg.genre_id ";

	protected GameGenreDao() {
		connectionManager = new ConnectionManager();
	}

	public static GameGenreDao getInstance() {
		if (instance == null) {
			instance = new GameGenreDao();
		}
		return instance;
	}

	public GameGenre create(GameGenre gameGenre) throws SQLException {
		String insert = "INSERT INTO GameGenre(app_id,genre_id) VALUES(?,?);";
		try (Connection connection = connectionManager.getConnection();
				PreparedStatement statement = connection.prepareStatement(insert)) {
			statement.setInt(1, gameGenre.getGame().getAppId());
			statement.setInt(2, gameGenre.getGenre().getGenreId());
			statement.executeUpdate();
			return gameGenre;
		}
	}

	/** Moves a game from one genre to another (the pair is the primary key). */
	public GameGenre updateGenre(GameGenre gameGenre, Genre newGenre) throws SQLException {
		String update = "UPDATE GameGenre SET genre_id=? WHERE app_id=? AND genre_id=?;";
		try (Connection connection = connectionManager.getConnection();
				PreparedStatement statement = connection.prepareStatement(update)) {
			statement.setInt(1, newGenre.getGenreId());
			statement.setInt(2, gameGenre.getGame().getAppId());
			statement.setInt(3, gameGenre.getGenre().getGenreId());
			statement.executeUpdate();
			gameGenre.setGenre(newGenre);
			return gameGenre;
		}
	}

	public GameGenre delete(GameGenre gameGenre) throws SQLException {
		String delete = "DELETE FROM GameGenre WHERE app_id=? AND genre_id=?;";
		try (Connection connection = connectionManager.getConnection();
				PreparedStatement statement = connection.prepareStatement(delete)) {
			statement.setInt(1, gameGenre.getGame().getAppId());
			statement.setInt(2, gameGenre.getGenre().getGenreId());
			statement.executeUpdate();
			return null;
		}
	}

	public GameGenre getGameGenre(int appId, int genreId) throws SQLException {
		String select = SELECT_COLUMNS + "WHERE gg.app_id=? AND gg.genre_id=?;";
		try (Connection connection = connectionManager.getConnection();
				PreparedStatement statement = connection.prepareStatement(select)) {
			statement.setInt(1, appId);
			statement.setInt(2, genreId);
			try (ResultSet results = statement.executeQuery()) {
				if (results.next()) {
					return extractGameGenre(results);
				}
				return null;
			}
		}
	}

	/** All genres of one game, by genre name. */
	public List<GameGenre> getGameGenresByAppId(int appId) throws SQLException {
		List<GameGenre> gameGenres = new ArrayList<GameGenre>();
		String select = SELECT_COLUMNS + "WHERE gg.app_id=? ORDER BY ge.name;";
		try (Connection connection = connectionManager.getConnection();
				PreparedStatement statement = connection.prepareStatement(select)) {
			statement.setInt(1, appId);
			try (ResultSet results = statement.executeQuery()) {
				while (results.next()) {
					gameGenres.add(extractGameGenre(results));
				}
			}
		}
		return gameGenres;
	}

	/** One page of the games in a genre, by game name. */
	public List<GameGenre> getGameGenresByGenreId(int genreId, int offset, int limit)
			throws SQLException {
		List<GameGenre> gameGenres = new ArrayList<GameGenre>();
		String select = SELECT_COLUMNS +
			"WHERE gg.genre_id=? ORDER BY g.name,g.app_id LIMIT ? OFFSET ?;";
		try (Connection connection = connectionManager.getConnection();
				PreparedStatement statement = connection.prepareStatement(select)) {
			statement.setInt(1, genreId);
			statement.setInt(2, limit);
			statement.setInt(3, offset);
			try (ResultSet results = statement.executeQuery()) {
				while (results.next()) {
					gameGenres.add(extractGameGenre(results));
				}
			}
		}
		return gameGenres;
	}

	public int getGameCountByGenreId(int genreId) throws SQLException {
		String select = "SELECT COUNT(*) FROM GameGenre WHERE genre_id=?;";
		try (Connection connection = connectionManager.getConnection();
				PreparedStatement statement = connection.prepareStatement(select)) {
			statement.setInt(1, genreId);
			try (ResultSet results = statement.executeQuery()) {
				results.next();
				return results.getInt(1);
			}
		}
	}

	private GameGenre extractGameGenre(ResultSet results) throws SQLException {
		Game game = new Game(results.getInt("app_id"));
		game.setName(results.getString("game_name"));
		Genre genre = new Genre(results.getInt("genre_id"), results.getString("genre_name"));
		return new GameGenre(game, genre);
	}
}
