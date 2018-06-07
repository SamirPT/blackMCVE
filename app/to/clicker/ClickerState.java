package to.clicker;

/**
 * Created by arkady on 02/04/16.
 */
public class ClickerState {
	private Integer totalIn;
	private Integer totalOut;
	private Integer men;
	private Integer women;
	private Integer capacity;

	public ClickerState() {
	}

	public ClickerState(Integer totalIn, Integer totalOut, Integer men, Integer women, Integer capacity) {
		this.totalIn = totalIn;
		this.totalOut = totalOut;
		this.men = men;
		this.women = women;
		this.capacity = capacity;
	}

	public Integer getTotalIn() {
		return totalIn;
	}

	public void setTotalIn(Integer totalIn) {
		this.totalIn = totalIn;
	}

	public Integer getTotalOut() {
		return totalOut;
	}

	public void setTotalOut(Integer totalOut) {
		this.totalOut = totalOut;
	}

	public Integer getMen() {
		return men;
	}

	public void setMen(Integer men) {
		this.men = men;
	}

	public Integer getWomen() {
		return women;
	}

	public void setWomen(Integer women) {
		this.women = women;
	}

	public Integer getCapacity() {
		return capacity;
	}

	public void setCapacity(Integer capacity) {
		this.capacity = capacity;
	}
}
