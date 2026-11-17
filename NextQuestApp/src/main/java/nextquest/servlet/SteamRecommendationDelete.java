package nextquest.servlet;

import nextquest.dal.SteamRecommendationDao;
import nextquest.model.SteamRecommendation;

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
 * Deletes a Steam recommendation.
 */
@WebServlet("/steamrecommendationdelete")
public class SteamRecommendationDelete extends HttpServlet {
	protected SteamRecommendationDao recommendationDao;

	@Override
	public void init() throws ServletException {
		recommendationDao = SteamRecommendationDao.getInstance();
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
		req.getRequestDispatcher("/SteamRecommendationDelete.jsp").forward(req, resp);
	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Map<String, String> messages = new HashMap<String, String>();
		req.setAttribute("messages", messages);
		String idParam = req.getParameter("steamRecommendationId");
		try {
			int id = Integer.parseInt(idParam == null ? "" : idParam.trim());
			SteamRecommendation recommendation = recommendationDao.getSteamRecommendationById(id);
			if (recommendation == null) {
				messages.put("error", "Steam recommendation not found.");
			} else {
				recommendationDao.delete(recommendation);
				messages.put("success", "Deleted Steam recommendation id=" + id);
			}
		} catch (NumberFormatException e) {
			messages.put("error", "Steam recommendation id must be a number.");
		} catch (SQLException e) {
			e.printStackTrace();
			throw new IOException(e);
		}
		req.getRequestDispatcher("/SteamRecommendationDelete.jsp").forward(req, resp);
	}
}
