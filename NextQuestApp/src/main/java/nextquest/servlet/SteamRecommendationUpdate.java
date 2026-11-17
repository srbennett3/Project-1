package nextquest.servlet;

import nextquest.dal.GameDao;
import nextquest.dal.SteamRecommendationDao;
import nextquest.model.Game;
import nextquest.model.SteamRecommendation;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Updates a Steam recommendation.
 */
@WebServlet("/steamrecommendationupdate")
public class SteamRecommendationUpdate extends HttpServlet {
	protected SteamRecommendationDao recommendationDao;
	protected GameDao gameDao;

	@Override
	public void init() throws ServletException {
		recommendationDao = SteamRecommendationDao.getInstance();
		gameDao = GameDao.getInstance();
	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Map<String, String> messages = new HashMap<String, String>();
		req.setAttribute("messages", messages);
		String idParam = req.getParameter("steamRecommendationId");
		if (idParam == null || idParam.trim().isEmpty()) {
			messages.put("error", "Please provide a Steam recommendation id.");
		} else {
			try {
				SteamRecommendation recommendation =
					recommendationDao.getSteamRecommendationById(Integer.parseInt(idParam.trim()));
				if (recommendation == null) {
					messages.put("error", "Steam recommendation not found.");
				}
				req.setAttribute("recommendation", recommendation);
			} catch (NumberFormatException e) {
				messages.put("error", "Steam recommendation id must be a number.");
			} catch (SQLException e) {
				e.printStackTrace();
				throw new IOException(e);
			}
		}
		req.getRequestDispatcher("/SteamRecommendationUpdate.jsp").forward(req, resp);
	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Map<String, String> messages = new HashMap<String, String>();
		req.setAttribute("messages", messages);
		String idParam = req.getParameter("steamRecommendationId");
		try {
			int id = Integer.parseInt(idParam == null ? "" : idParam.trim());
			SteamRecommendation existing = recommendationDao.getSteamRecommendationById(id);
			if (existing == null) {
				messages.put("error", "Steam recommendation not found.");
			} else {
				req.setAttribute("recommendation", existing);
				String appIdParam = req.getParameter("appId");
				int appId = Integer.parseInt(appIdParam == null ? "" : appIdParam.trim());
				Game game = gameDao.getGameById(appId);
				if (game == null) {
					messages.put("error", "Game not found.");
				} else {
					SteamRecommendation recommendation = new SteamRecommendation(id, game,
						parseInteger(req.getParameter("authorId")),
						parseBoolean(req.getParameter("isRecommended")),
						parseDecimal(req.getParameter("hoursPlayed")),
						parseDate(req.getParameter("recommendationDate")));
					recommendationDao.update(recommendation);
					req.setAttribute("recommendation", recommendation);
					messages.put("success", "Updated Steam recommendation id=" + id);
				}
			}
		} catch (NumberFormatException e) {
			messages.put("error", "Recommendation id, appId, authorId, and hoursPlayed must be valid numbers.");
		} catch (IllegalArgumentException e) {
			messages.put("error", "isRecommended must be true or false and the date must be YYYY-MM-DD.");
		} catch (SQLException e) {
			e.printStackTrace();
			throw new IOException(e);
		}
		req.getRequestDispatcher("/SteamRecommendationUpdate.jsp").forward(req, resp);
	}

	private Integer parseInteger(String value) {
		return value == null || value.trim().isEmpty() ? null : Integer.valueOf(value.trim());
	}

	private BigDecimal parseDecimal(String value) {
		return value == null || value.trim().isEmpty() ? null : new BigDecimal(value.trim());
	}

	private Boolean parseBoolean(String value) {
		if (value == null || value.trim().isEmpty()) {
			return null;
		}
		if ("true".equalsIgnoreCase(value.trim())) {
			return Boolean.TRUE;
		}
		if ("false".equalsIgnoreCase(value.trim())) {
			return Boolean.FALSE;
		}
		throw new IllegalArgumentException("Invalid boolean.");
	}

	private java.util.Date parseDate(String value) {
		return value == null || value.trim().isEmpty()
			? null : java.sql.Date.valueOf(value.trim());
	}
}
