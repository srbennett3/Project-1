package nextquest.servlet;

import nextquest.dal.SteamReviewDao;
import nextquest.model.SteamReview;

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
 * Deletes a Steam review.
 */
@WebServlet("/steamreviewdelete")
public class SteamReviewDelete extends HttpServlet {
	protected SteamReviewDao reviewDao;

	@Override
	public void init() throws ServletException {
		reviewDao = SteamReviewDao.getInstance();
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
		req.getRequestDispatcher("/SteamReviewDelete.jsp").forward(req, resp);
	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Map<String, String> messages = new HashMap<String, String>();
		req.setAttribute("messages", messages);
		String idParam = req.getParameter("steamReviewId");
		try {
			int id = Integer.parseInt(idParam == null ? "" : idParam.trim());
			SteamReview review = reviewDao.getSteamReviewById(id);
			if (review == null) {
				messages.put("error", "Steam review not found.");
			} else {
				reviewDao.delete(review);
				messages.put("success", "Deleted Steam review id=" + id);
			}
		} catch (NumberFormatException e) {
			messages.put("error", "Steam review id must be a number.");
		} catch (SQLException e) {
			e.printStackTrace();
			throw new IOException(e);
		}
		req.getRequestDispatcher("/SteamReviewDelete.jsp").forward(req, resp);
	}
}
