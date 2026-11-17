package nextquest.servlet;

import nextquest.dal.UserDao;
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
 * User delete page.
 */
@WebServlet("/userdelete")
public class UserDelete extends HttpServlet {

	protected UserDao userDao;

	@Override
	public void init() throws ServletException {
		userDao = UserDao.getInstance();
	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Map<String, String> messages = new HashMap<String, String>();
		req.setAttribute("messages", messages);

		String userIdParam = req.getParameter("userId");
		if (userIdParam == null || userIdParam.trim().isEmpty()) {
			messages.put("error", "Please provide a userId.");
		} else {
			try {
				User user = userDao.getUserById(Integer.parseInt(userIdParam));
				req.setAttribute("user", user);
				if (user == null) {
					messages.put("error", "User not found.");
				}
			} catch (NumberFormatException e) {
				messages.put("error", "userId must be a number.");
			} catch (SQLException e) {
				e.printStackTrace();
				throw new IOException(e);
			}
		}

		req.getRequestDispatcher("/UserDelete.jsp").forward(req, resp);
	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Map<String, String> messages = new HashMap<String, String>();
		req.setAttribute("messages", messages);

		String userIdParam = req.getParameter("userId");
		if (userIdParam == null || userIdParam.trim().isEmpty()) {
			messages.put("error", "Please provide a userId.");
		} else {
			try {
				int userId = Integer.parseInt(userIdParam);
				User user = userDao.getUserById(userId);
				if (user == null) {
					messages.put("error", "User not found.");
				} else {
					userDao.delete(user);
					messages.put("success", "Deleted user id=" + userId);
				}
			} catch (NumberFormatException e) {
				messages.put("error", "userId must be a number.");
			} catch (SQLException e) {
				e.printStackTrace();
				throw new IOException(e);
			}
		}

		req.getRequestDispatcher("/UserDelete.jsp").forward(req, resp);
	}
}
