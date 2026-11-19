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
 * ?filterUserId= shows only that user's surveys and preselects the user in the form.
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
	 * Show every recommendation survey, or one user's surveys.
	 * req is an HttpServletRequest. resp is an HttpServletResponse.
	 * Returns nothing. Throws ServletException and IOException.
	 */
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Map<String, String> messages = new HashMap<String, String>();
		req.setAttribute("messages", messages);
		loadPage(req, messages);
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
			messages.put("error", "Please choose a user.");
		} else {
			try {
				int userId = Integer.parseInt(userIdParam.trim());
				// Keep the chosen user selected if the page comes back with an error.
				req.setAttribute("selectedUserId", userId);
				// Load the user before the insert. The dropdown only offers existing
				// users, but a stale page can still send an id that was deleted.
				User user = userDao.getUserById(userId);
				if (user == null) {
					messages.put("error", "User id=" + userId + " not found.");
				} else {
					RecommendationSurvey survey = new RecommendationSurvey(user,
						SurveyForm.genre(req.getParameter("preferredGenre"), null),
						SurveyForm.platform(req.getParameter("preferredPlatform"), null),
						SurveyForm.playStyle(req.getParameter("playStyle"), null));
					// Insert the survey and read the new survey_id.
					survey = surveyDao.create(survey);
					messages.put("success", "Created survey id=" + survey.getSurveyId());
					req.setAttribute("createdSurvey", survey);
				}
			} catch (NumberFormatException e) {
				messages.put("error", "userId must be a number.");
			} catch (IllegalArgumentException e) {
				messages.put("error", e.getMessage());
			} catch (SQLException e) {
				e.printStackTrace();
				throw new IOException(e);
			}
		}

		loadPage(req, messages);
		req.getRequestDispatcher("/RecommendationSurveyList.jsp").forward(req, resp);
	}

	/**
	 * Put the form choices and the survey list on the request.
	 * req is an HttpServletRequest. messages holds the page messages.
	 * Returns nothing. Throws IOException.
	 */
	private void loadPage(HttpServletRequest req, Map<String, String> messages)
			throws IOException {
		try {
			SurveyForm.loadChoices(req);
			Integer filterUserId = parseFilter(req.getParameter("filterUserId"), messages);
			List<RecommendationSurvey> surveys;
			if (filterUserId == null) {
				// Load every survey.
				surveys = surveyDao.getAllRecommendationSurveys();
			} else {
				// Load only this user's surveys, and preselect the user in the form.
				surveys = surveyDao.getRecommendationSurveysByUserId(filterUserId);
				req.setAttribute("filterUserId", filterUserId);
				if (req.getAttribute("selectedUserId") == null) {
					req.setAttribute("selectedUserId", filterUserId);
				}
			}
			req.setAttribute("surveys", surveys);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new IOException(e);
		}
	}

	/**
	 * Read the filterUserId parameter.
	 * value is a String. Returns the user id, or null when blank or not a number.
	 */
	private Integer parseFilter(String value, Map<String, String> messages) {
		if (value == null || value.trim().isEmpty()) {
			return null;
		}
		try {
			return Integer.parseInt(value.trim());
		} catch (NumberFormatException e) {
			messages.putIfAbsent("error", "filterUserId must be a number.");
			return null;
		}
	}
}
