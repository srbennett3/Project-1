package nextquest.model;

/**
 * Model for the GameDeveloper junction table: primary key (app_id, developer_id).
 */
public class GameDeveloper {
	protected Game game;
	protected Developer developer;

	public GameDeveloper(Game game, Developer developer) {
		this.game = game;
		this.developer = developer;
	}

	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public Developer getDeveloper() {
		return developer;
	}

	public void setDeveloper(Developer developer) {
		this.developer = developer;
	}
}
