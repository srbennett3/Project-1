package nextquest.servlet;

import nextquest.dal.GameDao;
import nextquest.dal.ReviewDao;
import nextquest.dal.UserDao;
import nextquest.model.Game;
import nextquest.model.Review;
import nextquest.model.User;

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
 * Updates a review.
 */
@WebServlet("/reviewupdate")
public class ReviewUpdate extends HttpServlet {
	protected ReviewDao reviewDao;
	protected GameDao gameDao;
	protected UserDao userDao;

	/**
	 * Load ReviewDao, GameDao, and UserDao.
	 * Returns nothing. Throws ServletException.
	 */
	@Override
	public void init() throws ServletException {
		reviewDao = ReviewDao.getInstance();
		gameDao = GameDao.getInstance();
		userDao = UserDao.getInstance();
	}

	/**
	 * Load one review into the update form.
	 * req is an HttpServletRequest. resp is an HttpServletResponse.
	 * Returns nothing. Throws ServletException and IOException.
	 */
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Map<String, String> messages = new HashMap<String, String>();
		req.setAttribute("messages", messages);
		String idParam = req.getParameter("reviewId");
		if (idParam == null || idParam.trim().isEmpty()) {
			messages.put("error", "Please provide a review id.");
		} else {
			try {
				// Load the review for this reviewId.
				Review review = reviewDao.getReviewById(Integer.parseInt(idParam.trim()));
				if (review == null) {
					messages.put("error", "Review not found.");
				}
				req.setAttribute("review", review);
			} catch (NumberFormatException e) {
				messages.put("error", "Review id must be a number.");
			} catch (SQLException e) {
				e.printStackTrace();
				throw new IOException(e);
			}
		}
		req.getRequestDispatcher("/ReviewUpdate.jsp").forward(req, resp);
	}

	/**
	 * Write the form fields onto one review.
	 * req is an HttpServletRequest. resp is an HttpServletResponse.
	 * Returns nothing. Throws ServletException and IOException.
	 */
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Map<String, String> messages = new HashMap<String, String>();
		req.setAttribute("messages", messages);
		String idParam = req.getParameter("reviewId");
		try {
			int id = Integer.parseInt(idParam == null ? "" : idParam.trim());
			Review existing = reviewDao.getReviewById(id);
			if (existing == null) {
				messages.put("error", "Review not found.");
			} else {
				req.setAttribute("review", existing);
				String appIdParam = req.getParameter("appId");
				String userIdParam = req.getParameter("userId");
				int appId = Integer.parseInt(appIdParam == null ? "" : appIdParam.trim());
				int userId = Integer.parseInt(userIdParam == null ? "" : userIdParam.trim());
				// Load the game and the user before the update.
				Game game = gameDao.getGameById(appId);
				User user = userDao.getUserById(userId);
				if (game == null) {
					messages.put("error", "Game not found.");
				} else if (user == null) {
					messages.put("error", "User not found.");
				} else {
					Review review = new Review(id, game, user,
						parseBoolean(req.getParameter("isRecommended")),
						parseDecimal(req.getParameter("hoursPlayed")),
						parseDate(req.getParameter("reviewDate")));
					// Write every column for this review_id.
					reviewDao.update(review);
					req.setAttribute("review", review);
					messages.put("success", "Updated review id=" + id);
				}
			}
		} catch (NumberFormatException e) {
			messages.put("error", "Review id, appId, userId, and hoursPlayed must be valid numbers.");
		} catch (IllegalArgumentException e) {
			messages.put("error", "isRecommended must be true or false and the date must be YYYY-MM-DD.");
		} catch (SQLException e) {
			// A duplicate user and game stays on the form.
			if (e.getErrorCode() == 1062) {
				messages.put("error", "This user already reviewed this game.");
			} else {
				e.printStackTrace();
				throw new IOException(e);
			}
		}
		req.getRequestDispatcher("/ReviewUpdate.jsp").forward(req, resp);
	}

	/**
	 * Read a decimal from the form.
	 * value is a String. Returns a BigDecimal, or null when value is blank.
	 */
	private BigDecimal parseDecimal(String value) {
		if (value == null || value.trim().isEmpty()) {
			return null;
		}
		return new BigDecimal(value.trim());
	}

	/**
	 * Read true or false from the form.
	 * value is a String. Returns a Boolean, or null when value is blank.
	 */
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

	/**
	 * Read a date from the form.
	 * value is a String. Returns a Date, or null when value is blank.
	 */
	private java.util.Date parseDate(String value) {
		if (value == null || value.trim().isEmpty()) {
			return null;
		}
		return java.sql.Date.valueOf(value.trim());
	}
}
