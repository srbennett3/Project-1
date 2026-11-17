package nextquest.model;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Model for the SteamReview table.
 */
public class SteamReview {
	protected int steamReviewId;
	protected Game game;
	protected Long authorSteamId;
	protected Boolean isRecommended;
	protected BigDecimal hoursPlayed;
	protected Date reviewDate;
	protected String language;

	public SteamReview(int steamReviewId, Game game, Long authorSteamId,
			Boolean isRecommended, BigDecimal hoursPlayed, Date reviewDate, String language) {
		this.steamReviewId = steamReviewId;
		this.game = game;
		this.authorSteamId = authorSteamId;
		this.isRecommended = isRecommended;
		this.hoursPlayed = hoursPlayed;
		this.reviewDate = reviewDate;
		this.language = language;
	}

	public SteamReview(Game game, Long authorSteamId, Boolean isRecommended,
			BigDecimal hoursPlayed, Date reviewDate, String language) {
		this.game = game;
		this.authorSteamId = authorSteamId;
		this.isRecommended = isRecommended;
		this.hoursPlayed = hoursPlayed;
		this.reviewDate = reviewDate;
		this.language = language;
	}

	public int getSteamReviewId() {
		return steamReviewId;
	}

	public void setSteamReviewId(int steamReviewId) {
		this.steamReviewId = steamReviewId;
	}

	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public Long getAuthorSteamId() {
		return authorSteamId;
	}

	public void setAuthorSteamId(Long authorSteamId) {
		this.authorSteamId = authorSteamId;
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

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}
}
