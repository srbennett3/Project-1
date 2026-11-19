package nextquest.servlet;

import nextquest.dal.GameDao;
import nextquest.dal.GameDeveloperDao;
import nextquest.dal.GameGenreDao;
import nextquest.dal.GameVideoDao;
import nextquest.dal.SteamRecommendationDao;
import nextquest.dal.SteamReviewDao;
import nextquest.model.Game;
import nextquest.model.GameVideo;
import nextquest.model.SteamRecommendation;
import nextquest.model.SteamReview;
import nextquest.model.VideoType;

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
 * Game detail page. Shows one game plus its trailers/screenshots and
 * an embedded HLS player (advanced feature) for the selected trailer.
 */
@WebServlet("/gamedetail")
public class GameDetail extends HttpServlet {
	private static final int STEAM_DATA_PAGE_SIZE = 100;

	protected GameDao gameDao;
	protected GameVideoDao gameVideoDao;
	protected SteamRecommendationDao steamRecommendationDao;
	protected SteamReviewDao steamReviewDao;
	protected GameGenreDao gameGenreDao;
	protected GameDeveloperDao gameDeveloperDao;

	@Override
	public void init() throws ServletException {
		gameDao = GameDao.getInstance();
		gameVideoDao = GameVideoDao.getInstance();
		steamRecommendationDao = SteamRecommendationDao.getInstance();
		steamReviewDao = SteamReviewDao.getInstance();
		gameGenreDao = GameGenreDao.getInstance();
		gameDeveloperDao = GameDeveloperDao.getInstance();
	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Map<String, String> messages = new HashMap<String, String>();
		req.setAttribute("messages", messages);

		String appIdParam = req.getParameter("appId");
		if (appIdParam == null || appIdParam.trim().isEmpty()) {
			messages.put("error", "Please provide an appId.");
			req.getRequestDispatcher("/GameDetail.jsp").forward(req, resp);
			return;
		}

		try {
			int appId = Integer.parseInt(appIdParam.trim());
			Game game = gameDao.getGameById(appId);
			req.setAttribute("game", game);

			if (game != null) {
				loadSteamData(req, appId, messages);
				req.setAttribute("gameGenres", gameGenreDao.getGameGenresByAppId(appId));
				req.setAttribute("gameDevelopers", gameDeveloperDao.getGameDevelopersByAppId(appId));

				List<GameVideo> trailers = gameVideoDao.getVideosByAppIdAndType(
					appId, VideoType.TRAILER);
				List<GameVideo> screenshots = gameVideoDao.getVideosByAppIdAndType(
					appId, VideoType.SCREENSHOT);
				req.setAttribute("trailers", trailers);
				req.setAttribute("screenshots", screenshots);

				// Select the trailer to play: ?videoId= overrides, otherwise
				// the first trailer with a non-null video_url.
				String selectedParam = req.getParameter("videoId");
				GameVideo selected = null;
				if (selectedParam != null && !selectedParam.trim().isEmpty()) {
					int selectedId = Integer.parseInt(selectedParam);
					selected = gameVideoDao.getGameVideoById(selectedId);
				}
				if (selected == null) {
					for (GameVideo t : trailers) {
						if (t.getVideoUrl() != null && !t.getVideoUrl().isEmpty()) {
							selected = t;
							break;
						}
					}
				}
				req.setAttribute("selectedVideo", selected);
			}
		} catch (NumberFormatException e) {
			messages.put("error", "appId must be a number.");
		} catch (SQLException e) {
			e.printStackTrace();
			throw new IOException(e);
		}

		req.getRequestDispatcher("/GameDetail.jsp").forward(req, resp);
	}

	private void loadSteamData(HttpServletRequest req, int appId,
			Map<String, String> messages) throws SQLException {
		int reviewCount = steamReviewDao.getSteamReviewCountByAppId(appId);
		int recommendationCount =
			steamRecommendationDao.getSteamRecommendationCountByAppId(appId);
		int reviewPages = pageCount(reviewCount);
		int recommendationPages = pageCount(recommendationCount);
		int reviewPage = parsePage(req.getParameter("reviewPage"), "reviewPage", messages);
		int recommendationPage = parsePage(
			req.getParameter("recommendationPage"), "recommendationPage", messages);
		reviewPage = Math.min(reviewPage, reviewPages);
		recommendationPage = Math.min(recommendationPage, recommendationPages);

		List<SteamReview> reviews = steamReviewDao.getSteamReviewsByAppId(appId,
			(reviewPage - 1) * STEAM_DATA_PAGE_SIZE, STEAM_DATA_PAGE_SIZE);
		List<SteamRecommendation> recommendations =
			steamRecommendationDao.getSteamRecommendationsByAppId(appId,
				(recommendationPage - 1) * STEAM_DATA_PAGE_SIZE, STEAM_DATA_PAGE_SIZE);

		req.setAttribute("steamReviews", reviews);
		req.setAttribute("steamReviewCount", reviewCount);
		req.setAttribute("steamReviewPage", reviewPage);
		req.setAttribute("steamReviewPages", reviewPages);
		req.setAttribute("steamRecommendations", recommendations);
		req.setAttribute("steamRecommendationCount", recommendationCount);
		req.setAttribute("steamRecommendationPage", recommendationPage);
		req.setAttribute("steamRecommendationPages", recommendationPages);
	}

	private int pageCount(int totalRecords) {
		return Math.max(1, (totalRecords + STEAM_DATA_PAGE_SIZE - 1) / STEAM_DATA_PAGE_SIZE);
	}

	private int parsePage(String value, String parameterName, Map<String, String> messages) {
		if (value == null || value.trim().isEmpty()) {
			return 1;
		}
		try {
			return Math.max(1, Integer.parseInt(value.trim()));
		} catch (NumberFormatException e) {
			messages.put("error", parameterName + " must be a number.");
			return 1;
		}
	}
}
