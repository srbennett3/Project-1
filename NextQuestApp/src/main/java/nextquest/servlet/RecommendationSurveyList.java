package nextquest.servlet;

import nextquest.dal.RecommendationSurveyDao;
import nextquest.dal.UserDao;
import nextquest.model.RecommendationSurvey;
import nextquest.model.User;

import java.io.IOException;
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
 * Lists and creates recommendation surveys.
 */
@WebServlet("/surveys")
public class RecommendationSurveyList extends HttpServlet {
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
	 * Show every recommendation survey.
	 * req is an HttpServletRequest. resp is an HttpServletResponse.
	 * Returns nothing. Throws ServletException and IOException.
	 */
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Map<String, String> messages = new HashMap<String, String>();
		req.setAttribute("messages", messages);
		try {
			// Load every survey.
			List<RecommendationSurvey> surveys = surveyDao.getAllRecommendationSurveys();
			req.setAttribute("surveys", surveys);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new IOException(e);
		}
		req.getRequestDispatcher("/RecommendationSurveyList.jsp").forward(req, resp);
	}

	/**
	 * Insert one survey from the form, then show the list.
	 * req is an HttpServletRequest. resp is an HttpServletResponse.
	 * Returns nothing. Throws ServletException and IOException.
	 */
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Map<String, String> messages = new HashMap<String, String>();
		req.setAttribute("messages", messages);
		String userIdParam = req.getParameter("userId");

		if (userIdParam == null || userIdParam.trim().isEmpty()) {
			messages.put("error", "userId is required.");
		} else {
			try {
				int userId = Integer.parseInt(userIdParam.trim());
				// Load the user before the insert.
				User user = userDao.getUserById(userId);
				if (user == null) {
					messages.put("error", "User not found.");
				} else {
					RecommendationSurvey survey = new RecommendationSurvey(user,
						parseText(req.getParameter("preferredGenre")),
						parseText(req.getParameter("preferredPlatform")),
						parseText(req.getParameter("playStyle")));
					// Insert the survey and read the new survey_id.
					survey = surveyDao.create(survey);
					messages.put("success", "Created survey id=" + survey.getSurveyId());
				}
			} catch (NumberFormatException e) {
				messages.put("error", "userId must be a number.");
			} catch (SQLException e) {
				e.printStackTrace();
				throw new IOException(e);
			}
		}

		try {
			List<RecommendationSurvey> surveys = surveyDao.getAllRecommendationSurveys();
			req.setAttribute("surveys", surveys);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new IOException(e);
		}
		req.getRequestDispatcher("/RecommendationSurveyList.jsp").forward(req, resp);
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
