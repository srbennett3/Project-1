package nextquest.model;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Model for the SteamRecommendation table.
 */
public class SteamRecommendation {
	protected int steamRecommendationId;
	protected Game game;
	protected Integer authorId;
	protected Boolean isRecommended;
	protected BigDecimal hoursPlayed;
	protected Date recommendationDate;

	public SteamRecommendation(int steamRecommendationId, Game game, Integer authorId,
			Boolean isRecommended, BigDecimal hoursPlayed, Date recommendationDate) {
		this.steamRecommendationId = steamRecommendationId;
		this.game = game;
		this.authorId = authorId;
		this.isRecommended = isRecommended;
		this.hoursPlayed = hoursPlayed;
		this.recommendationDate = recommendationDate;
	}

	public SteamRecommendation(Game game, Integer authorId, Boolean isRecommended,
			BigDecimal hoursPlayed, Date recommendationDate) {
		this.game = game;
		this.authorId = authorId;
		this.isRecommended = isRecommended;
		this.hoursPlayed = hoursPlayed;
		this.recommendationDate = recommendationDate;
	}

	public int getSteamRecommendationId() {
		return steamRecommendationId;
	}

	public void setSteamRecommendationId(int steamRecommendationId) {
		this.steamRecommendationId = steamRecommendationId;
	}

	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public Integer getAuthorId() {
		return authorId;
	}

	public void setAuthorId(Integer authorId) {
		this.authorId = authorId;
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

	public Date getRecommendationDate() {
		return recommendationDate;
	}

	public void setRecommendationDate(Date recommendationDate) {
		this.recommendationDate = recommendationDate;
	}
}
