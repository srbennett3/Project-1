package nextquest.model;

/**
 * Model for the Developer table.
 */
public class Developer {
	protected int developerId;
	protected String name;

	public Developer(int developerId, String name) {
		this.developerId = developerId;
		this.name = name;
	}

	public Developer(int developerId) {
		this.developerId = developerId;
	}

	public Developer(String name) {
		this.name = name;
	}

	public int getDeveloperId() {
		return developerId;
	}

	public void setDeveloperId(int developerId) {
		this.developerId = developerId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
