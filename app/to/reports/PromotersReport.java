package to.reports;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arkady on 01/04/16.
 */
public class PromotersReport extends Report {
	private List<Promoter> promoters = new ArrayList<>();

	public PromotersReport(ArrayList<Promoter> promoters, List<PromotedReservationItem> reservationItems) {
		this.setReservationItems(reservationItems);
		this.promoters = promoters;
	}

	public List<Promoter> getPromoters() {
		return promoters;
	}

	public void setPromoters(List<Promoter> promoters) {
		this.promoters = promoters;
	}
}
