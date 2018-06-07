package to;

/**
 * Created by arkady on 03/04/16.
 */
public class TableWithSeating {
	private TableInfo tableInfo;
	private ReservationInfo reservationInfo;

	public TableInfo getTableInfo() {
		return tableInfo;
	}

	public void setTableInfo(TableInfo tableInfo) {
		this.tableInfo = tableInfo;
	}

	public ReservationInfo getReservationInfo() {
		return reservationInfo;
	}

	public void setReservationInfo(ReservationInfo reservationInfo) {
		this.reservationInfo = reservationInfo;
	}
}
