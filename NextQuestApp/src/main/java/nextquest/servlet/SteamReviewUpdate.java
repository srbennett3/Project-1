package nextquest.servlet;

import nextquest.dal.GameDao;
import nextquest.dal.SteamReviewDao;
import nextquest.model.Game;
import nextquest.model.SteamReview;

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
 * Updates a Steam review.
 */
@WebServlet("/steamreviewupdate")
public class SteamReviewUpdate extends HttpServlet {
	protected SteamReviewDao reviewDao;
	protected GameDao gameDao;

	@Override
	public void init() throws ServletException {
		reviewDao = SteamReviewDao.getInstance();
		gameDao = GameDao.getInstance();
	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Map<String, String> messages = new HashMap<String, String>();
		req.setAttribute("messages", messages);
		String idParam = req.getParameter("steamReviewId");
		if (idParam == null || idParam.trim().isEmpty()) {
			messages.put("error", "Please provide a Steam review id.");
		} else {
			try {
				SteamReview review = reviewDao.getSteamReviewById(Integer.parseInt(idParam.trim()));
				if (review == null) {
					messages.put("error", "Steam review not found.");
				}
				req.setAttribute("review", review);
			} catch (NumberFormatException e) {
				messages.put("error", "Steam review id must be a number.");
			} catch (SQLException e) {
				e.printStackTrace();
				throw new IOException(e);
			}
		}
		req.getRequestDispatcher("/SteamReviewUpdate.jsp").forward(req, resp);
	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Map<String, String> messages = new HashMap<String, String>();
		req.setAttribute("messages", messages);
		String idParam = req.getParameter("steamReviewId");
		try {
			int id = Integer.parseInt(idParam == null ? "" : idParam.trim());
			SteamReview existing = reviewDao.getSteamReviewById(id);
			if (existing == null) {
				messages.put("error", "Steam review not found.");
			} else {
				req.setAttribute("review", existing);
				String appIdParam = req.getParameter("appId");
				int appId = Integer.parseInt(appIdParam == null ? "" : appIdParam.trim());
				Game game = gameDao.getGameById(appId);
				if (game == null) {
					messages.put("error", "Game not found.");
				} else {
					SteamReview review = new SteamReview(id, game,
						parseLong(req.getParameter("authorSteamId")),
						parseBoolean(req.getParameter("isRecommended")),
						parseDecimal(req.getParameter("hoursPlayed")),
						parseDate(req.getParameter("reviewDate")),
						req.getParameter("language"));
					reviewDao.update(review);
					req.setAttribute("review", review);
					messages.put("success", "Updated Steam review id=" + id);
				}
			}
		} catch (NumberFormatException e) {
			messages.put("error", "Review id, appId, authorSteamId, and hoursPlayed must be valid numbers.");
		} catch (IllegalArgumentException e) {
			messages.put("error", "isRecommended must be true or false and the date must be YYYY-MM-DD.");
		} catch (SQLException e) {
			e.printStackTrace();
			throw new IOException(e);
		}
		req.getRequestDispatcher("/SteamReviewUpdate.jsp").forward(req, resp);
	}

	private Long parseLong(String value) {
		return value == null || value.trim().isEmpty() ? null : Long.valueOf(value.trim());
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
