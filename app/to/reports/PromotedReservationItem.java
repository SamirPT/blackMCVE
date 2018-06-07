package to.reports;

/**
 * Created by arkady on 02/04/16.
 */
public class PromotedReservationItem extends ReservationItem {
	private Promoter promotionCompany;

	public PromotedReservationItem() {
	}

	public PromotedReservationItem(Promoter promotionCompany) {
		this.promotionCompany = promotionCompany;
	}

	public PromotedReservationItem(String date, String userpic, String fullname, String reservedBy, String arrived, Short rating, Integer minSpend, Integer actual, Promoter promotionCompany) {
		super(date, userpic, fullname, reservedBy, arrived, rating, minSpend, actual);
		this.promotionCompany = promotionCompany;
	}

	public PromotedReservationItem(ReservationItem reservationItem) {
		super(reservationItem.getDate(), reservationItem.getUserpic(), reservationItem.getFullName(),
				reservationItem.getReservedBy(), reservationItem.getArrived(), reservationItem.getRating(),
				reservationItem.getMinSpend(), reservationItem.getActualSpent());
	}

	public Promoter getPromotionCompany() {
		return promotionCompany;
	}

	public void setPromotionCompany(Promoter promotionCompany) {
		this.promotionCompany = promotionCompany;
	}
}
