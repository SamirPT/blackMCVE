package util;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlQuery;
import play.Logger;

/**
 * Created by arkady on 16/06/16.
 */
public class GuestAppHelper {

	public static Short getOverallGuestRating(Long guestId) {
		String sql = "select avg(stars) as average from feedback where user_id=:guest_id and stars is not null;";
		SqlQuery sqlQuery = Ebean.createSqlQuery(sql);
		sqlQuery.setParameter("guest_id", guestId);

		float result = 0;
		try {
			Float average = sqlQuery.findUnique().getFloat("average");
			if (average != null) {
				result = MathUtil.round(average, 1);
			}
		} catch (Exception e) {
			Logger.warn("Can't get overall rating for user_id=" + guestId, e);
		}

		return Float.valueOf(result).shortValue();
	}
}
