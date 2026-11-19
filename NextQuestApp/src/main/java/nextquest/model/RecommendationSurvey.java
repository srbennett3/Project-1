package nextquest.model;

import java.util.Date;

/**
 * Model for the RecommendationSurvey table.
 */
public class RecommendationSurvey {
	protected int surveyId;
	protected User user;
	protected String preferredGenre;
	protected String preferredPlatform;
	protected String playStyle;
	protected Date updatedAt;

	public RecommendationSurvey(int surveyId, User user, String preferredGenre,
			String preferredPlatform, String playStyle, Date updatedAt) {
		this.surveyId = surveyId;
		this.user = user;
		this.preferredGenre = preferredGenre;
		this.preferredPlatform = preferredPlatform;
		this.playStyle = playStyle;
		this.updatedAt = updatedAt;
	}

	public RecommendationSurvey(User user, String preferredGenre,
			String preferredPlatform, String playStyle) {
		this.user = user;
		this.preferredGenre = preferredGenre;
		this.preferredPlatform = preferredPlatform;
		this.playStyle = playStyle;
	}

	public int getSurveyId() {
		return surveyId;
	}

	public void setSurveyId(int surveyId) {
		this.surveyId = surveyId;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getPreferredGenre() {
		return preferredGenre;
	}

	public void setPreferredGenre(String preferredGenre) {
		this.preferredGenre = preferredGenre;
	}

	public String getPreferredPlatform() {
		return preferredPlatform;
	}

	public void setPreferredPlatform(String preferredPlatform) {
		this.preferredPlatform = preferredPlatform;
	}

	public String getPlayStyle() {
		return playStyle;
	}

	public void setPlayStyle(String playStyle) {
		this.playStyle = playStyle;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}
}
