package to.clicker;

/**
 * Created by arkady on 03/04/16.
 */
public class Alert {
	private Long venueId;
	private AlertType type;
	private String vipName;

	public Alert() {
	}

	public Alert(Long venueId, AlertType type, String vipName) {
		this.venueId = venueId;
		this.type = type;
		this.vipName = vipName;
	}

	public Long getVenueId() {
		return venueId;
	}

	public void setVenueId(Long venueId) {
		this.venueId = venueId;
	}

	public AlertType getType() {
		return type;
	}

	public void setType(AlertType type) {
		this.type = type;
	}

	public String getVipName() {
		return vipName;
	}

	public void setVipName(String vipName) {
		this.vipName = vipName;
	}
}
