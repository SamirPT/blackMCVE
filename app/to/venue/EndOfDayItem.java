package to.venue;

import to.TableInfo;

import java.util.List;

/**
 * Created by arkady on 20/07/16.
 */
public class EndOfDayItem {
	private TableInfo tableInfo;
	private List<EODReservationInfo> reservations;

	public EndOfDayItem() {
	}

	public EndOfDayItem(TableInfo tableInfo, List<EODReservationInfo> reservations) {
		this.tableInfo = tableInfo;
		this.reservations = reservations;
	}

	public TableInfo getTableInfo() {
		return tableInfo;
	}

	public void setTableInfo(TableInfo tableInfo) {
		this.tableInfo = tableInfo;
	}

	public List<EODReservationInfo> getReservations() {
		return reservations;
	}

	public void setReservations(List<EODReservationInfo> reservations) {
		this.reservations = reservations;
	}
}
