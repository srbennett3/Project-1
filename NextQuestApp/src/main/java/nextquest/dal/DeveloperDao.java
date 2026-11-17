package nextquest.dal;

import nextquest.model.Developer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for the Developer table.
 */
public class DeveloperDao {
	protected ConnectionManager connectionManager;

	private static DeveloperDao instance = null;

	protected DeveloperDao() {
		connectionManager = new ConnectionManager();
	}

	public static DeveloperDao getInstance() {
		if (instance == null) {
			instance = new DeveloperDao();
		}
		return instance;
	}

	public Developer create(Developer developer) throws SQLException {
		String insertDeveloper = "INSERT INTO Developer(name) VALUES(?);";
		Connection connection = null;
		PreparedStatement insertStmt = null;
		ResultSet resultKey = null;
		try {
			connection = connectionManager.getConnection();
			insertStmt = connection.prepareStatement(insertDeveloper,
				Statement.RETURN_GENERATED_KEYS);
			insertStmt.setString(1, developer.getName());
			insertStmt.executeUpdate();

			resultKey = insertStmt.getGeneratedKeys();
			int developerId = -1;
			if (resultKey.next()) {
				developerId = resultKey.getInt(1);
			} else {
				throw new SQLException("Unable to retrieve auto-generated key.");
			}
			developer.setDeveloperId(developerId);
			return developer;
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			close(connection, insertStmt, resultKey);
		}
	}

	public Developer updateName(Developer developer, String newName) throws SQLException {
		String updateDeveloper = "UPDATE Developer SET name=? WHERE developer_id=?;";
		Connection connection = null;
		PreparedStatement updateStmt = null;
		try {
			connection = connectionManager.getConnection();
			updateStmt = connection.prepareStatement(updateDeveloper);
			updateStmt.setString(1, newName);
			updateStmt.setInt(2, developer.getDeveloperId());
			updateStmt.executeUpdate();
			developer.setName(newName);
			return developer;
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			close(connection, updateStmt, null);
		}
	}

	public Developer delete(Developer developer) throws SQLException {
		String deleteDeveloper = "DELETE FROM Developer WHERE developer_id=?;";
		Connection connection = null;
		PreparedStatement deleteStmt = null;
		try {
			connection = connectionManager.getConnection();
			deleteStmt = connection.prepareStatement(deleteDeveloper);
			deleteStmt.setInt(1, developer.getDeveloperId());
			deleteStmt.executeUpdate();
			return null;
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			close(connection, deleteStmt, null);
		}
	}

	public Developer getDeveloperById(int developerId) throws SQLException {
		String selectDeveloper = "SELECT developer_id,name FROM Developer WHERE developer_id=?;";
		Connection connection = null;
		PreparedStatement selectStmt = null;
		ResultSet results = null;
		try {
			connection = connectionManager.getConnection();
			selectStmt = connection.prepareStatement(selectDeveloper);
			selectStmt.setInt(1, developerId);
			results = selectStmt.executeQuery();

			if (results.next()) {
				int resultId = results.getInt("developer_id");
				String name = results.getString("name");
				return new Developer(resultId, name);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			close(connection, selectStmt, results);
		}
		return null;
	}

	public List<Developer> getAllDevelopers() throws SQLException {
		List<Developer> developers = new ArrayList<Developer>();
		String selectDevelopers = "SELECT developer_id,name FROM Developer ORDER BY name;";
		Connection connection = null;
		PreparedStatement selectStmt = null;
		ResultSet results = null;
		try {
			connection = connectionManager.getConnection();
			selectStmt = connection.prepareStatement(selectDevelopers);
			results = selectStmt.executeQuery();

			while (results.next()) {
				int developerId = results.getInt("developer_id");
				String name = results.getString("name");
				developers.add(new Developer(developerId, name));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			close(connection, selectStmt, results);
		}
		return developers;
	}

	public List<Developer> getDevelopersByName(String name) throws SQLException {
		List<Developer> developers = new ArrayList<Developer>();
		String selectDevelopers = "SELECT developer_id,name FROM Developer WHERE name=?;";
		Connection connection = null;
		PreparedStatement selectStmt = null;
		ResultSet results = null;
		try {
			connection = connectionManager.getConnection();
			selectStmt = connection.prepareStatement(selectDevelopers);
			selectStmt.setString(1, name);
			results = selectStmt.executeQuery();

			while (results.next()) {
				int developerId = results.getInt("developer_id");
				String resultName = results.getString("name");
				developers.add(new Developer(developerId, resultName));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			close(connection, selectStmt, results);
		}
		return developers;
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
