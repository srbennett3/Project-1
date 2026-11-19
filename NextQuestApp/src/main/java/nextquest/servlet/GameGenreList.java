package nextquest.servlet;

import nextquest.dal.GameDao;
import nextquest.dal.GameGenreDao;
import nextquest.dal.GenreDao;
import nextquest.model.Game;
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
 * The genres of one game. GET ?appId= lists them; POST adds a genre to the game.
 */
@WebServlet("/gamegenres")
public class GameGenreList extends HttpServlet {

	protected GameGenreDao gameGenreDao;
	protected GameDao gameDao;
	protected GenreDao genreDao;

	@Override
	public void init() throws ServletException {
		gameGenreDao = GameGenreDao.getInstance();
		gameDao = GameDao.getInstance();
		genreDao = GenreDao.getInstance();
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
		req.getRequestDispatcher("/GameGenreList.jsp").forward(req, resp);
	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Map<String, String> messages = new HashMap<String, String>();
		req.setAttribute("messages", messages);
		Integer appId = null;
		try {
			appId = Params.parseInt(req.getParameter("appId"), "appId");
			Integer genreId = Params.parseInt(req.getParameter("genreId"), "genreId");
			if (appId != null && genreId == null) {
				messages.put("error", "Please choose a genre.");
			} else if (appId != null) {
				Game game = gameDao.getGameById(appId);
				Genre genre = genreDao.getGenreById(genreId);
				if (game != null && genre != null) {
					gameGenreDao.create(new GameGenre(game, genre));
					messages.put("success", "Added genre " + genre.getName() + ".");
				} else if (genre == null) {
					messages.put("error", "Genre not found.");
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
		try {
			load(req, appId, messages);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new IOException(e);
		}
		req.getRequestDispatcher("/GameGenreList.jsp").forward(req, resp);
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
		req.setAttribute("gameGenres", gameGenreDao.getGameGenresByAppId(appId));
		req.setAttribute("allGenres", genreDao.getAllGenres());
	}
}
