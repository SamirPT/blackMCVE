package to.venue;

/**
 * Created by arkady on 28/05/16.
 */
public class State {
	private Boolean bsClosed;
	private Boolean glClosed;
	private Integer minSpend;
	private Integer minBottles;

	public Boolean getBsClosed() {
		return bsClosed;
	}

	public void setBsClosed(Boolean bsClosed) {
		this.bsClosed = bsClosed;
	}

	public Boolean getGlClosed() {
		return glClosed;
	}

	public void setGlClosed(Boolean glClosed) {
		this.glClosed = glClosed;
	}

	public Integer getMinSpend() {
		return minSpend;
	}

	public void setMinSpend(Integer minSpend) {
		this.minSpend = minSpend;
	}

	public Integer getMinBottles() {
		return minBottles;
	}

	public void setMinBottles(Integer minBottles) {
		this.minBottles = minBottles;
	}
}
