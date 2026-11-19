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
 * Shows the games one survey recommends (GET ?surveyId=). Each game can be saved
 * to the survey owner's saved games from the page.
 */
@WebServlet("/recommendations")
public class SurveyRecommendations extends HttpServlet {
	private static final int RECOMMENDATION_COUNT = 20;

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
	 * Show the recommended games for one survey.
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
				} else {
					// The DAO only fills in user_id; load the user for the username.
					User user = userDao.getUserById(survey.getUser().getUserId());
					if (user != null) {
						survey.setUser(user);
					}
					req.setAttribute("survey", survey);
					req.setAttribute("games",
						surveyDao.getRecommendedGames(survey, RECOMMENDATION_COUNT));
				}
			} catch (NumberFormatException e) {
				messages.put("error", "Survey id must be a number.");
			} catch (SQLException e) {
				e.printStackTrace();
				throw new IOException(e);
			}
		}
		req.getRequestDispatcher("/SurveyRecommendations.jsp").forward(req, resp);
	}
}
