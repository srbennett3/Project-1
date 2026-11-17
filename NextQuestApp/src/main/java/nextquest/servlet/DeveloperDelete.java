package nextquest.servlet;

import nextquest.dal.DeveloperDao;
import nextquest.model.Developer;

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
 * Developer delete page.
 */
@WebServlet("/developerdelete")
public class DeveloperDelete extends HttpServlet {

	protected DeveloperDao developerDao;

	@Override
	public void init() throws ServletException {
		developerDao = DeveloperDao.getInstance();
	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Map<String, String> messages = new HashMap<String, String>();
		req.setAttribute("messages", messages);

		String developerIdParam = req.getParameter("developerId");
		if (developerIdParam == null || developerIdParam.trim().isEmpty()) {
			messages.put("error", "Please provide a developerId.");
		} else {
			try {
				Developer developer = developerDao.getDeveloperById(
					Integer.parseInt(developerIdParam));
				req.setAttribute("developer", developer);
			} catch (NumberFormatException e) {
				messages.put("error", "developerId must be a number.");
			} catch (SQLException e) {
				e.printStackTrace();
				throw new IOException(e);
			}
		}

		req.getRequestDispatcher("/DeveloperDelete.jsp").forward(req, resp);
	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Map<String, String> messages = new HashMap<String, String>();
		req.setAttribute("messages", messages);

		String developerIdParam = req.getParameter("developerId");
		if (developerIdParam == null || developerIdParam.trim().isEmpty()) {
			messages.put("error", "Please provide a developerId.");
		} else {
			try {
				int developerId = Integer.parseInt(developerIdParam);
				Developer developer = developerDao.getDeveloperById(developerId);
				if (developer == null) {
					messages.put("error", "Developer not found.");
				} else {
					developerDao.delete(developer);
					messages.put("success", "Deleted developer id=" + developerId);
				}
			} catch (NumberFormatException e) {
				messages.put("error", "developerId must be a number.");
			} catch (SQLException e) {
				e.printStackTrace();
				throw new IOException(e);
			}
		}

		req.getRequestDispatcher("/DeveloperDelete.jsp").forward(req, resp);
	}
}
