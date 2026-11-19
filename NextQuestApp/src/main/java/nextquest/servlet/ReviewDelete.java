package nextquest.servlet;

import nextquest.dal.ReviewDao;
import nextquest.model.Review;

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
 * Deletes a review.
 */
@WebServlet("/reviewdelete")
public class ReviewDelete extends HttpServlet {
	protected ReviewDao reviewDao;

	/**
	 * Load ReviewDao. Returns nothing. Throws ServletException.
	 */
	@Override
	public void init() throws ServletException {
		reviewDao = ReviewDao.getInstance();
	}

	/**
	 * Load one review into the delete form.
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
		req.getRequestDispatcher("/ReviewDelete.jsp").forward(req, resp);
	}

	/**
	 * Delete the review for the posted reviewId.
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
			// Load the review, then delete that row.
			Review review = reviewDao.getReviewById(id);
			if (review == null) {
				messages.put("error", "Review not found.");
			} else {
				reviewDao.delete(review);
				messages.put("success", "Deleted review id=" + id);
			}
		} catch (NumberFormatException e) {
			messages.put("error", "Review id must be a number.");
		} catch (SQLException e) {
			e.printStackTrace();
			throw new IOException(e);
		}
		req.getRequestDispatcher("/ReviewDelete.jsp").forward(req, resp);
	}
}
