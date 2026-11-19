package nextquest.servlet;

import nextquest.dal.DeveloperDao;
import nextquest.dal.GameDao;
import nextquest.dal.GameDeveloperDao;
import nextquest.model.Developer;
import nextquest.model.Game;
import nextquest.model.GameDeveloper;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * The developers of one game. GET ?appId= lists them; POST credits the game to a
 * developer by name, creating the Developer row when the name is new.
 */
@WebServlet("/gamedevelopers")
public class GameDeveloperList extends HttpServlet {

	protected GameDeveloperDao gameDeveloperDao;
	protected GameDao gameDao;
	protected DeveloperDao developerDao;

	@Override
	public void init() throws ServletException {
		gameDeveloperDao = GameDeveloperDao.getInstance();
		gameDao = GameDao.getInstance();
		developerDao = DeveloperDao.getInstance();
	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Map<String, String> messages = new HashMap<String, String>();
		req.setAttribute("messages", messages);
		try {
			load(req, Params.parseInt(req.getParameter("appId"), "appId"), messages);
		} catch (IllegalArgumentException e) {
			messages.put("error", e.getMessage());
		} catch (SQLException e) {
			e.printStackTrace();
			throw new IOException(e);
		}
		req.getRequestDispatcher("/GameDeveloperList.jsp").forward(req, resp);
	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Map<String, String> messages = new HashMap<String, String>();
		req.setAttribute("messages", messages);
		Integer appId = null;
		try {
			appId = Params.parseInt(req.getParameter("appId"), "appId");
			String name = Params.trimToNull(req.getParameter("developerName"));
			if (appId != null && name == null) {
				messages.put("error", "Please enter a developer name.");
			} else if (appId != null) {
				Game game = gameDao.getGameById(appId);
				if (game != null) {
					Developer developer = findOrCreate(developerDao, name);
					gameDeveloperDao.create(new GameDeveloper(game, developer));
					messages.put("success", "Added developer " + developer.getName() + ".");
				}
			}
		} catch (IllegalArgumentException e) {
			messages.put("error", e.getMessage());
		} catch (SQLIntegrityConstraintViolationException e) {
			messages.put("error", "That developer is already credited on this game.");
		} catch (SQLException e) {
			e.printStackTrace();
			throw new IOException(e);
		}
		try {
			load(req, appId, messages);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new IOException(e);
		}
		req.getRequestDispatcher("/GameDeveloperList.jsp").forward(req, resp);
	}

	/** Returns the Developer with this exact name, creating it if there is none. */
	static Developer findOrCreate(DeveloperDao developerDao, String name) throws SQLException {
		List<Developer> matches = developerDao.getDevelopersByName(name);
		if (!matches.isEmpty()) {
			return matches.get(0);
		}
		return developerDao.create(new Developer(name));
	}

	private void load(HttpServletRequest req, Integer appId, Map<String, String> messages)
			throws SQLException {
		if (appId == null) {
			messages.putIfAbsent("error", "Please provide an appId.");
			return;
		}
		Game game = gameDao.getGameById(appId);
		req.setAttribute("game", game);
		if (game == null) {
			messages.put("error", "Game app_id=" + appId + " not found.");
			return;
		}
		req.setAttribute("gameDevelopers", gameDeveloperDao.getGameDevelopersByAppId(appId));
	}
}
