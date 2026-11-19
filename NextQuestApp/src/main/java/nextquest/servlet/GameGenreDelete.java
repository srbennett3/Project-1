package nextquest.servlet;

import nextquest.dal.GameGenreDao;
import nextquest.model.GameGenre;

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
 * Removes a genre from a game (?appId=&genreId=). GET confirms; POST deletes.
 * The Genre row itself is kept.
 */
@WebServlet("/gamegenredelete")
public class GameGenreDelete extends HttpServlet {

	protected GameGenreDao gameGenreDao;

	@Override
	public void init() throws ServletException {
		gameGenreDao = GameGenreDao.getInstance();
	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Map<String, String> messages = new HashMap<String, String>();
		req.setAttribute("messages", messages);
		try {
			req.setAttribute("gameGenre", GameGenreUpdate.find(req, messages));
		} catch (SQLException e) {
			e.printStackTrace();
			throw new IOException(e);
		}
		req.getRequestDispatcher("/GameGenreDelete.jsp").forward(req, resp);
	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Map<String, String> messages = new HashMap<String, String>();
		req.setAttribute("messages", messages);
		try {
			GameGenre gameGenre = GameGenreUpdate.find(req, messages);
			if (gameGenre != null) {
				gameGenreDao.delete(gameGenre);
				messages.put("success", "Removed genre " + gameGenre.getGenre().getName()
					+ " from " + gameGenre.getGame().getName() + ".");
				req.setAttribute("appId", gameGenre.getGame().getAppId());
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new IOException(e);
		}
		req.getRequestDispatcher("/GameGenreDelete.jsp").forward(req, resp);
	}
}
