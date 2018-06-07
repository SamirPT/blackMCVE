package to.reports;

import to.SearchUserInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arkady on 01/04/16.
 */
public class EmployeesReport extends Report {
	private List<SearchUserInfo> employees = new ArrayList<>();

	public EmployeesReport(List<SearchUserInfo> employees, List<? extends ReservationItem> reservationItems) {
		setReservationItems(reservationItems);
		this.employees = employees;
	}

	public List<SearchUserInfo> getEmployees() {
		return employees;
	}

	public void setEmployees(List<SearchUserInfo> employees) {
		this.employees = employees;
	}
}
