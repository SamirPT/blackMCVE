package to.clicker;

/**
 * Created by arkady on 03/04/16.
 */
public enum AlertType {
	POLICE("Police"),
	FIRE("Fire"),
	LIQUOR_BOARD("Liquor board"),
	MLS("Police"),
	CAPACITY_REACHED("Capacity reached"),
	VIP_ARRIVAL("VIP arrival");

	private final String label;

	AlertType(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
}
