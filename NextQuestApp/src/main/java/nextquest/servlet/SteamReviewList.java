package nextquest.servlet;

import nextquest.dal.GameDao;
import nextquest.dal.SteamReviewDao;
import nextquest.model.Game;
import nextquest.model.SteamReview;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Lists and creates Steam reviews.
 */
@WebServlet("/steamreviews")
public class SteamReviewList extends HttpServlet {
	private static final int PAGE_SIZE = 100;

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
		try {
			int page = 1;
			String pageParam = req.getParameter("page");
			if (pageParam != null && !pageParam.trim().isEmpty()) {
				try {
					page = Integer.parseInt(pageParam.trim());
				} catch (NumberFormatException e) {
					messages.put("error", "Page must be a number.");
				}
			}
			loadPage(req, page);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new IOException(e);
		}
		req.getRequestDispatcher("/SteamReviewList.jsp").forward(req, resp);
	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Map<String, String> messages = new HashMap<String, String>();
		req.setAttribute("messages", messages);
		String appIdParam = req.getParameter("appId");

		if (appIdParam == null || appIdParam.trim().isEmpty()) {
			messages.put("error", "appId is required.");
		} else {
			try {
				int appId = Integer.parseInt(appIdParam.trim());
				Game game = gameDao.getGameById(appId);
				if (game == null) {
					messages.put("error", "Game not found.");
				} else {
					SteamReview review = new SteamReview(game,
						parseLong(req.getParameter("authorSteamId")),
						parseBoolean(req.getParameter("isRecommended")),
						parseDecimal(req.getParameter("hoursPlayed")),
						parseDate(req.getParameter("reviewDate")),
						req.getParameter("language"));
					reviewDao.create(review);
					messages.put("success", "Created Steam review id=" + review.getSteamReviewId());
				}
			} catch (NumberFormatException e) {
				messages.put("error", "appId, authorSteamId, and hoursPlayed must be valid numbers.");
			} catch (IllegalArgumentException e) {
				messages.put("error", "isRecommended must be true or false and the date must be YYYY-MM-DD.");
			} catch (SQLException e) {
				e.printStackTrace();
				throw new IOException(e);
			}
		}

		try {
			loadPage(req, 1);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new IOException(e);
		}
		req.getRequestDispatcher("/SteamReviewList.jsp").forward(req, resp);
	}

	private void loadPage(HttpServletRequest req, int requestedPage) throws SQLException {
		int totalRecords = reviewDao.getSteamReviewCount();
		int totalPages = Math.max(1, (totalRecords + PAGE_SIZE - 1) / PAGE_SIZE);
		int page = Math.max(1, Math.min(requestedPage, totalPages));
		List<SteamReview> reviews = reviewDao.getSteamReviews(
			(page - 1) * PAGE_SIZE, PAGE_SIZE);
		req.setAttribute("reviews", reviews);
		req.setAttribute("currentPage", page);
		req.setAttribute("totalPages", totalPages);
		req.setAttribute("totalRecords", totalRecords);
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
