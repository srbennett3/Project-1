package nextquest.servlet;

import nextquest.dal.UserDao;
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
 * User list page. GET renders the list; POST creates a new User.
 */
@WebServlet("/users")
public class UserList extends HttpServlet {

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

		try {
			List<User> users = userDao.getAllUsers();
			req.setAttribute("users", users);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new IOException(e);
		}

		req.getRequestDispatcher("/UserList.jsp").forward(req, resp);
	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Map<String, String> messages = new HashMap<String, String>();
		req.setAttribute("messages", messages);

		String username = req.getParameter("username");
		String email = req.getParameter("email");

		if (username == null || username.trim().isEmpty()) {
			messages.put("error", "Username is required.");
		} else if (email == null || email.trim().isEmpty()) {
			messages.put("error", "Email is required.");
		} else {
			try {
				User user = new User(username.trim(), email.trim());
				user = userDao.create(user);
				messages.put("success", "Created user id=" + user.getUserId());
			} catch (SQLException e) {
				e.printStackTrace();
				throw new IOException(e);
			}
		}

		try {
			List<User> users = userDao.getAllUsers();
			req.setAttribute("users", users);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new IOException(e);
		}

		req.getRequestDispatcher("/UserList.jsp").forward(req, resp);
	}
}
