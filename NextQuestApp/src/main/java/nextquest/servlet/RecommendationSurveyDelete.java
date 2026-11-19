package nextquest.servlet;

import nextquest.dal.RecommendationSurveyDao;
import nextquest.model.RecommendationSurvey;

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
 * Deletes a recommendation survey.
 */
@WebServlet("/surveydelete")
public class RecommendationSurveyDelete extends HttpServlet {
	protected RecommendationSurveyDao surveyDao;

	/**
	 * Load RecommendationSurveyDao. Returns nothing. Throws ServletException.
	 */
	@Override
	public void init() throws ServletException {
		surveyDao = RecommendationSurveyDao.getInstance();
	}

	/**
	 * Load one survey into the delete form.
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
		req.getRequestDispatcher("/RecommendationSurveyDelete.jsp").forward(req, resp);
	}

	/**
	 * Delete the survey for the posted surveyId.
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
			// Load the survey, then delete that row.
			RecommendationSurvey survey = surveyDao.getRecommendationSurveyById(id);
			if (survey == null) {
				messages.put("error", "Survey not found.");
			} else {
				surveyDao.delete(survey);
				messages.put("success", "Deleted survey id=" + id);
			}
		} catch (NumberFormatException e) {
			messages.put("error", "Survey id must be a number.");
		} catch (SQLException e) {
			e.printStackTrace();
			throw new IOException(e);
		}
		req.getRequestDispatcher("/RecommendationSurveyDelete.jsp").forward(req, resp);
	}
}
