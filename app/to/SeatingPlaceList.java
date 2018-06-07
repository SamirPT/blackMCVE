package to;

import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arkady on 20/03/16.
 */
public class SeatingPlaceList {

	@ApiModelProperty(required = false, readOnly = true)
	List<TableInfo> table = new ArrayList<>();
	@ApiModelProperty(required = false, readOnly = true)
	List<TableInfo> standup = new ArrayList<>();
	@ApiModelProperty(required = false, readOnly = true)
	List<TableInfo> bar = new ArrayList<>();

	public List<TableInfo> getTable() {
		return table;
	}

	public void setTable(List<TableInfo> table) {
		this.table = table;
	}

	public List<TableInfo> getStandup() {
		return standup;
	}

	public void setStandup(List<TableInfo> standup) {
		this.standup = standup;
	}

	public List<TableInfo> getBar() {
		return bar;
	}

	public void setBar(List<TableInfo> bar) {
		this.bar = bar;
	}
}
