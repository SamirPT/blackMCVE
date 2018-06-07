package to.venue;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arkady on 14/10/16.
 */
public class EndOfDayStatement {
	private List<EndOfDayItem> endOfDayItems;
	private boolean reportSent;

	public EndOfDayStatement() {
		endOfDayItems = new ArrayList<>();
	}

	public List<EndOfDayItem> getEndOfDayItems() {
		return endOfDayItems;
	}

	public void setEndOfDayItems(List<EndOfDayItem> endOfDayItems) {
		this.endOfDayItems = endOfDayItems;
	}

	public boolean isReportSent() {
		return reportSent;
	}

	public void setReportSent(boolean reportSent) {
		this.reportSent = reportSent;
	}
}
