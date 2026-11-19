package nextquest.servlet;

import nextquest.dal.RecommendationSurveyDao;
import nextquest.dal.UserDao;
import nextquest.model.RecommendationSurvey;
import nextquest.model.User;

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
 * Updates a recommendation survey.
 */
@WebServlet("/surveyupdate")
public class RecommendationSurveyUpdate extends HttpServlet {
	protected RecommendationSurveyDao surveyDao;
	protected UserDao userDao;

	/**
	 * Load RecommendationSurveyDao and UserDao.
	 * Returns nothing. Throws ServletException.
	 */
	@Override
	public void init() throws ServletException {
		surveyDao = RecommendationSurveyDao.getInstance();
		userDao = UserDao.getInstance();
	}

	/**
	 * Load one survey into the update form.
	 * req is an HttpServletRequest. resp is an HttpServletResponse.
	 * Returns nothing. Throws ServletException and IOException.
	 */
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Map<String, String> messages = new HashMap<String, String>();
		req.setAttribute("messages", messages);
		String idParam = req.getParameter("surveyId");
		if (idParam == null || idParam.trim().isEmpty()) {
			messages.put("error", "Please provide a survey id.");
		} else {
			try {
				// Load the survey for this surveyId.
				RecommendationSurvey survey = surveyDao.getRecommendationSurveyById(
					Integer.parseInt(idParam.trim()));
				if (survey == null) {
					messages.put("error", "Survey not found.");
				}
				req.setAttribute("survey", survey);
			} catch (NumberFormatException e) {
				messages.put("error", "Survey id must be a number.");
			} catch (SQLException e) {
				e.printStackTrace();
				throw new IOException(e);
			}
		}
		req.getRequestDispatcher("/RecommendationSurveyUpdate.jsp").forward(req, resp);
	}

	/**
	 * Write the form fields onto one survey.
	 * req is an HttpServletRequest. resp is an HttpServletResponse.
	 * Returns nothing. Throws ServletException and IOException.
	 */
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Map<String, String> messages = new HashMap<String, String>();
		req.setAttribute("messages", messages);
		String idParam = req.getParameter("surveyId");
		try {
			int id = Integer.parseInt(idParam == null ? "" : idParam.trim());
			RecommendationSurvey existing = surveyDao.getRecommendationSurveyById(id);
			if (existing == null) {
				messages.put("error", "Survey not found.");
			} else {
				req.setAttribute("survey", existing);
				String userIdParam = req.getParameter("userId");
				int userId = Integer.parseInt(userIdParam == null ? "" : userIdParam.trim());
				// Load the user before the update.
				User user = userDao.getUserById(userId);
				if (user == null) {
					messages.put("error", "User not found.");
				} else {
					RecommendationSurvey survey = new RecommendationSurvey(id, user,
						parseText(req.getParameter("preferredGenre")),
						parseText(req.getParameter("preferredPlatform")),
						parseText(req.getParameter("playStyle")),
						existing.getUpdatedAt());
					// Write every column for this survey_id.
					survey = surveyDao.update(survey);
					req.setAttribute("survey", survey);
					messages.put("success", "Updated survey id=" + id);
				}
			}
		} catch (NumberFormatException e) {
			messages.put("error", "Survey id and userId must be numbers.");
		} catch (SQLException e) {
			e.printStackTrace();
			throw new IOException(e);
		}
		req.getRequestDispatcher("/RecommendationSurveyUpdate.jsp").forward(req, resp);
	}

	/**
	 * Read a text field from the form.
	 * value is a String. Returns that text, or null when value is blank.
	 */
	private String parseText(String value) {
		if (value == null || value.trim().isEmpty()) {
			return null;
		}
		return value.trim();
	}
}
