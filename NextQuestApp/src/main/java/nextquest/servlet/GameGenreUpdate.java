package nextquest.servlet;

import nextquest.dal.GameGenreDao;
import nextquest.dal.GenreDao;
import nextquest.model.GameGenre;
import nextquest.model.Genre;

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
 * Replaces one genre of a game with another (?appId=&genreId=).
 */
@WebServlet("/gamegenreupdate")
public class GameGenreUpdate extends HttpServlet {

	protected GameGenreDao gameGenreDao;
	protected GenreDao genreDao;

	@Override
	public void init() throws ServletException {
		gameGenreDao = GameGenreDao.getInstance();
		genreDao = GenreDao.getInstance();
	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Map<String, String> messages = new HashMap<String, String>();
		req.setAttribute("messages", messages);
		try {
			req.setAttribute("gameGenre", find(req, messages));
			req.setAttribute("allGenres", genreDao.getAllGenres());
		} catch (SQLException e) {
			e.printStackTrace();
			throw new IOException(e);
		}
		req.getRequestDispatcher("/GameGenreUpdate.jsp").forward(req, resp);
	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Map<String, String> messages = new HashMap<String, String>();
		req.setAttribute("messages", messages);
		try {
			req.setAttribute("allGenres", genreDao.getAllGenres());
			GameGenre gameGenre = find(req, messages);
			req.setAttribute("gameGenre", gameGenre);
			if (gameGenre != null) {
				Integer newGenreId = Params.parseInt(req.getParameter("newGenreId"), "newGenreId");
				Genre newGenre = newGenreId == null ? null : genreDao.getGenreById(newGenreId);
				if (newGenre == null) {
					messages.put("error", "Please choose a genre.");
				} else {
					gameGenreDao.updateGenre(gameGenre, newGenre);
					messages.put("success", "Genre changed to " + newGenre.getName() + ".");
				}
			}
		} catch (IllegalArgumentException e) {
			messages.put("error", e.getMessage());
		} catch (SQLIntegrityConstraintViolationException e) {
			messages.put("error", "The game already has that genre.");
		} catch (SQLException e) {
			e.printStackTrace();
			throw new IOException(e);
		}
		req.getRequestDispatcher("/GameGenreUpdate.jsp").forward(req, resp);
	}

	static GameGenre find(HttpServletRequest req, Map<String, String> messages)
			throws SQLException {
		try {
			Integer appId = Params.parseInt(req.getParameter("appId"), "appId");
			Integer genreId = Params.parseInt(req.getParameter("genreId"), "genreId");
			if (appId == null || genreId == null) {
				messages.put("error", "Please provide an appId and a genreId.");
				return null;
			}
			GameGenre gameGenre = GameGenreDao.getInstance().getGameGenre(appId, genreId);
			if (gameGenre == null) {
				messages.put("error", "That game does not have that genre.");
			}
			return gameGenre;
		} catch (IllegalArgumentException e) {
			messages.put("error", e.getMessage());
			return null;
		}
	}
}
