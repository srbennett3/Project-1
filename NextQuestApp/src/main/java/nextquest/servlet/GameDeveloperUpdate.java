package nextquest.servlet;

import nextquest.dal.DeveloperDao;
import nextquest.dal.GameDeveloperDao;
import nextquest.model.Developer;
import nextquest.model.GameDeveloper;

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
 * Credits a game to a different developer (?appId=&developerId=).
 */
@WebServlet("/gamedeveloperupdate")
public class GameDeveloperUpdate extends HttpServlet {

	protected GameDeveloperDao gameDeveloperDao;
	protected DeveloperDao developerDao;

	@Override
	public void init() throws ServletException {
		gameDeveloperDao = GameDeveloperDao.getInstance();
		developerDao = DeveloperDao.getInstance();
	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Map<String, String> messages = new HashMap<String, String>();
		req.setAttribute("messages", messages);
		try {
			req.setAttribute("gameDeveloper", find(req, messages));
		} catch (SQLException e) {
			e.printStackTrace();
			throw new IOException(e);
		}
		req.getRequestDispatcher("/GameDeveloperUpdate.jsp").forward(req, resp);
	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Map<String, String> messages = new HashMap<String, String>();
		req.setAttribute("messages", messages);
		try {
			GameDeveloper gameDeveloper = find(req, messages);
			req.setAttribute("gameDeveloper", gameDeveloper);
			if (gameDeveloper != null) {
				String name = Params.trimToNull(req.getParameter("newDeveloperName"));
				if (name == null) {
					messages.put("error", "Please enter a developer name.");
				} else {
					Developer newDeveloper = GameDeveloperList.findOrCreate(developerDao, name);
					gameDeveloperDao.updateDeveloper(gameDeveloper, newDeveloper);
					messages.put("success", "Developer changed to " + newDeveloper.getName() + ".");
				}
			}
		} catch (SQLIntegrityConstraintViolationException e) {
			messages.put("error", "That developer is already credited on this game.");
		} catch (SQLException e) {
			e.printStackTrace();
			throw new IOException(e);
		}
		req.getRequestDispatcher("/GameDeveloperUpdate.jsp").forward(req, resp);
	}

	static GameDeveloper find(HttpServletRequest req, Map<String, String> messages)
			throws SQLException {
		try {
			Integer appId = Params.parseInt(req.getParameter("appId"), "appId");
			Integer developerId = Params.parseInt(req.getParameter("developerId"), "developerId");
			if (appId == null || developerId == null) {
				messages.put("error", "Please provide an appId and a developerId.");
				return null;
			}
			GameDeveloper gameDeveloper =
				GameDeveloperDao.getInstance().getGameDeveloper(appId, developerId);
			if (gameDeveloper == null) {
				messages.put("error", "That developer is not credited on that game.");
			}
			return gameDeveloper;
		} catch (IllegalArgumentException e) {
			messages.put("error", e.getMessage());
			return null;
		}
	}
}
