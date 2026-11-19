package nextquest.model;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Model for the Review table (reviews written by NextQuest users).
 * A user can review a game once: (user_id, app_id) is unique.
 */
public class Review {
	protected int reviewId;
	protected Game game;
	protected User user;
	protected Boolean isRecommended;
	protected BigDecimal hoursPlayed;
	protected Date reviewDate;

	public Review(int reviewId, Game game, User user, Boolean isRecommended,
			BigDecimal hoursPlayed, Date reviewDate) {
		this.reviewId = reviewId;
		this.game = game;
		this.user = user;
		this.isRecommended = isRecommended;
		this.hoursPlayed = hoursPlayed;
		this.reviewDate = reviewDate;
	}

	public Review(int reviewId) {
		this.reviewId = reviewId;
	}

	public Review(Game game, User user, Boolean isRecommended,
			BigDecimal hoursPlayed, Date reviewDate) {
		this.game = game;
		this.user = user;
		this.isRecommended = isRecommended;
		this.hoursPlayed = hoursPlayed;
		this.reviewDate = reviewDate;
	}

	public int getReviewId() {
		return reviewId;
	}

	public void setReviewId(int reviewId) {
		this.reviewId = reviewId;
	}

	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Boolean getIsRecommended() {
		return isRecommended;
	}

	public void setIsRecommended(Boolean isRecommended) {
		this.isRecommended = isRecommended;
	}

	public BigDecimal getHoursPlayed() {
		return hoursPlayed;
	}

	public void setHoursPlayed(BigDecimal hoursPlayed) {
		this.hoursPlayed = hoursPlayed;
	}

	public Date getReviewDate() {
		return reviewDate;
	}

	public void setReviewDate(Date reviewDate) {
		this.reviewDate = reviewDate;
	}
}
