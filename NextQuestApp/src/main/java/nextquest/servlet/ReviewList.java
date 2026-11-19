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
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Lists and creates reviews.
 */
@WebServlet("/reviews")
public class ReviewList extends HttpServlet {
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
	 * Show every review.
	 * req is an HttpServletRequest. resp is an HttpServletResponse.
	 * Returns nothing. Throws ServletException and IOException.
	 */
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Map<String, String> messages = new HashMap<String, String>();
		req.setAttribute("messages", messages);
		try {
			// Load every review.
			List<Review> reviews = reviewDao.getAllReviews();
			req.setAttribute("reviews", reviews);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new IOException(e);
		}
		req.getRequestDispatcher("/ReviewList.jsp").forward(req, resp);
	}

	/**
	 * Insert one review from the form, then show the list.
	 * req is an HttpServletRequest. resp is an HttpServletResponse.
	 * Returns nothing. Throws ServletException and IOException.
	 */
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Map<String, String> messages = new HashMap<String, String>();
		req.setAttribute("messages", messages);
		String appIdParam = req.getParameter("appId");
		String userIdParam = req.getParameter("userId");

		if (appIdParam == null || appIdParam.trim().isEmpty()
				|| userIdParam == null || userIdParam.trim().isEmpty()) {
			messages.put("error", "appId and userId are required.");
		} else {
			try {
				int appId = Integer.parseInt(appIdParam.trim());
				int userId = Integer.parseInt(userIdParam.trim());
				// Load the game and the user before the insert.
				Game game = gameDao.getGameById(appId);
				User user = userDao.getUserById(userId);
				if (game == null) {
					messages.put("error", "Game not found.");
				} else if (user == null) {
					messages.put("error", "User not found.");
				} else {
					Review review = new Review(game, user,
						parseBoolean(req.getParameter("isRecommended")),
						parseDecimal(req.getParameter("hoursPlayed")),
						parseDate(req.getParameter("reviewDate")));
					// Insert the review and read the new review_id.
					review = reviewDao.create(review);
					messages.put("success", "Created review id=" + review.getReviewId());
				}
			} catch (NumberFormatException e) {
				messages.put("error", "appId, userId, and hoursPlayed must be valid numbers.");
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
		}

		try {
			List<Review> reviews = reviewDao.getAllReviews();
			req.setAttribute("reviews", reviews);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new IOException(e);
		}
		req.getRequestDispatcher("/ReviewList.jsp").forward(req, resp);
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
