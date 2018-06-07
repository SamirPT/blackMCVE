package to;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arkady on 28/03/16.
 */
public class TablesList {
	private List<TableInfo> tablesList = new ArrayList<>();

	public TablesList() {
	}

	public TablesList(List<TableInfo> feedbackList) {
		this.tablesList = feedbackList;
	}

	public List<TableInfo> getTablesList() {
		return tablesList;
	}

	public void setTablesList(List<TableInfo> tablesList) {
		this.tablesList = tablesList;
	}
}
