package to.clicker;

/**
 * Created by arkady on 02/04/16.
 */
public class ClickerUpdate {
	private Short men = 0;
	private Short women = 0;

	public ClickerUpdate() {
	}

	public ClickerUpdate(Short men, Short women) {
		this.men = men;
		this.women = women;
	}

	public Short getMen() {
		return men;
	}

	public void setMen(Short men) {
		this.men = men;
	}

	public Short getWomen() {
		return women;
	}

	public void setWomen(Short women) {
		this.women = women;
	}
}
