package nextquest.model;

import java.util.Date;

/**
 * Model for the SavedGame table.
 * The primary key is (user_id, app_id); both foreign keys are held as objects.
 */
public class SavedGame {
	protected User user;
	protected Game game;
	protected Date savedAt;

	public SavedGame(User user, Game game, Date savedAt) {
		this.user = user;
		this.game = game;
		this.savedAt = savedAt;
	}

	public SavedGame(User user, Game game) {
		this.user = user;
		this.game = game;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public Date getSavedAt() {
		return savedAt;
	}

	public void setSavedAt(Date savedAt) {
		this.savedAt = savedAt;
	}
}
