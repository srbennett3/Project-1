package nextquest.model;

/**
 * Model for the GameGenre junction table: primary key (app_id, genre_id).
 */
public class GameGenre {
	protected Game game;
	protected Genre genre;

	public GameGenre(Game game, Genre genre) {
		this.game = game;
		this.genre = genre;
	}

	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public Genre getGenre() {
		return genre;
	}

	public void setGenre(Genre genre) {
		this.genre = genre;
	}
}
