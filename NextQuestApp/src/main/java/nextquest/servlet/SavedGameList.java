package nextquest.servlet;

import nextquest.dal.GameDao;
import nextquest.dal.SavedGameDao;
import nextquest.dal.UserDao;
import nextquest.model.Game;
import nextquest.model.SavedGame;
import nextquest.model.User;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A user's saved games. GET ?userId= lists them; POST saves a game for the user.
 */
@WebServlet("/savedgames")
public class SavedGameList extends HttpServlet {

	protected SavedGameDao savedGameDao;
	protected UserDao userDao;
	protected GameDao gameDao;

	@Override
	public void init() throws ServletException {
		savedGameDao = SavedGameDao.getInstance();
		userDao = UserDao.getInstance();
		gameDao = GameDao.getInstance();
	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Map<String, String> messages = new HashMap<String, String>();
		req.setAttribute("messages", messages);
		try {
			Integer userId = Params.parseInt(req.getParameter("userId"), "userId");
			if (userId == null) {
				messages.put("error", "Please provide a userId.");
			} else {
				loadUser(req, userId, messages);
			}
		} catch (IllegalArgumentException e) {
			messages.put("error", e.getMessage());
		} catch (SQLException e) {
			e.printStackTrace();
			throw new IOException(e);
		}
		req.getRequestDispatcher("/SavedGameList.jsp").forward(req, resp);
	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Map<String, String> messages = new HashMap<String, String>();
		req.setAttribute("messages", messages);
		Integer userId = null;
		try {
			userId = Params.parseInt(req.getParameter("userId"), "userId");
			Integer appId = Params.parseInt(req.getParameter("appId"), "appId");
			if (userId == null) {
				messages.put("error", "Please provide a userId.");
			} else if (appId == null) {
				messages.put("error", "Please provide an appId.");
			} else {
				User user = userDao.getUserById(userId);
				Game game = gameDao.getGameById(appId);
				if (user == null) {
					messages.put("error", "User not found.");
				} else if (game == null) {
					messages.put("error", "Game app_id=" + appId + " not found.");
				} else {
					savedGameDao.create(new SavedGame(user, game));
					messages.put("success", "Saved " + game.getName() + ".");
				}
			}
		} catch (IllegalArgumentException e) {
			messages.put("error", e.getMessage());
		} catch (SQLIntegrityConstraintViolationException e) {
			messages.put("error", "That game is already saved.");
		} catch (SQLException e) {
			e.printStackTrace();
			throw new IOException(e);
		}

		if (userId != null) {
			try {
				loadUser(req, userId, messages);
			} catch (SQLException e) {
				e.printStackTrace();
				throw new IOException(e);
			}
		}
		req.getRequestDispatcher("/SavedGameList.jsp").forward(req, resp);
	}

	private void loadUser(HttpServletRequest req, int userId, Map<String, String> messages)
			throws SQLException {
		User user = userDao.getUserById(userId);
		req.setAttribute("user", user);
		if (user == null) {
			messages.put("error", "User not found.");
		} else {
			req.setAttribute("savedGames", savedGameDao.getSavedGamesByUserId(userId));
		}
	}
}
