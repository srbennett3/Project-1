package nextquest.servlet;

import nextquest.dal.SavedGameDao;
import nextquest.model.SavedGame;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Removes a game from a user's saved games. GET confirms; POST deletes.
 */
@WebServlet("/savedgamedelete")
public class SavedGameDelete extends HttpServlet {

	protected SavedGameDao savedGameDao;

	@Override
	public void init() throws ServletException {
		savedGameDao = SavedGameDao.getInstance();
	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Map<String, String> messages = new HashMap<String, String>();
		req.setAttribute("messages", messages);
		try {
			SavedGame savedGame = find(req, messages);
			req.setAttribute("savedGame", savedGame);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new IOException(e);
		}
		req.getRequestDispatcher("/SavedGameDelete.jsp").forward(req, resp);
	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Map<String, String> messages = new HashMap<String, String>();
		req.setAttribute("messages", messages);
		try {
			SavedGame savedGame = find(req, messages);
			if (savedGame != null) {
				savedGameDao.delete(savedGame);
				messages.put("success", "Removed " + savedGame.getGame().getName()
					+ " from saved games.");
				req.setAttribute("deletedUserId", savedGame.getUser().getUserId());
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new IOException(e);
		}
		req.getRequestDispatcher("/SavedGameDelete.jsp").forward(req, resp);
	}

	private SavedGame find(HttpServletRequest req, Map<String, String> messages)
			throws SQLException {
		try {
			Integer userId = Params.parseInt(req.getParameter("userId"), "userId");
			Integer appId = Params.parseInt(req.getParameter("appId"), "appId");
			if (userId == null || appId == null) {
				messages.put("error", "Please provide a userId and an appId.");
				return null;
			}
			SavedGame savedGame = savedGameDao.getSavedGame(userId, appId);
			if (savedGame == null) {
				messages.put("error", "Saved game not found.");
			}
			return savedGame;
		} catch (IllegalArgumentException e) {
			messages.put("error", e.getMessage());
			return null;
		}
	}
}
