package nextquest.servlet;

import nextquest.dal.GameDeveloperDao;
import nextquest.model.GameDeveloper;

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
 * Removes a developer credit from a game (?appId=&developerId=). GET confirms;
 * POST deletes. The Developer row itself is kept.
 */
@WebServlet("/gamedeveloperdelete")
public class GameDeveloperDelete extends HttpServlet {

	protected GameDeveloperDao gameDeveloperDao;

	@Override
	public void init() throws ServletException {
		gameDeveloperDao = GameDeveloperDao.getInstance();
	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Map<String, String> messages = new HashMap<String, String>();
		req.setAttribute("messages", messages);
		try {
			req.setAttribute("gameDeveloper", GameDeveloperUpdate.find(req, messages));
		} catch (SQLException e) {
			e.printStackTrace();
			throw new IOException(e);
		}
		req.getRequestDispatcher("/GameDeveloperDelete.jsp").forward(req, resp);
	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Map<String, String> messages = new HashMap<String, String>();
		req.setAttribute("messages", messages);
		try {
			GameDeveloper gameDeveloper = GameDeveloperUpdate.find(req, messages);
			if (gameDeveloper != null) {
				gameDeveloperDao.delete(gameDeveloper);
				messages.put("success", "Removed developer " + gameDeveloper.getDeveloper().getName()
					+ " from " + gameDeveloper.getGame().getName() + ".");
				req.setAttribute("appId", gameDeveloper.getGame().getAppId());
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new IOException(e);
		}
		req.getRequestDispatcher("/GameDeveloperDelete.jsp").forward(req, resp);
	}
}
