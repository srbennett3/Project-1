package nextquest.servlet;

import nextquest.dal.DeveloperDao;
import nextquest.model.Developer;

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
 * Developer list page. GET renders the list; POST creates a new Developer.
 */
@WebServlet("/developers")
public class DeveloperList extends HttpServlet {

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

		try {
			List<Developer> developers = developerDao.getAllDevelopers();
			req.setAttribute("developers", developers);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new IOException(e);
		}

		req.getRequestDispatcher("/DeveloperList.jsp").forward(req, resp);
	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Map<String, String> messages = new HashMap<String, String>();
		req.setAttribute("messages", messages);

		String name = req.getParameter("name");
		if (name == null || name.trim().isEmpty()) {
			messages.put("error", "Developer name is required.");
		} else {
			try {
				Developer developer = new Developer(name.trim());
				developer = developerDao.create(developer);
				messages.put("success", "Created developer id=" + developer.getDeveloperId());
			} catch (SQLException e) {
				e.printStackTrace();
				throw new IOException(e);
			}
		}

		try {
			List<Developer> developers = developerDao.getAllDevelopers();
			req.setAttribute("developers", developers);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new IOException(e);
		}

		req.getRequestDispatcher("/DeveloperList.jsp").forward(req, resp);
	}
}
