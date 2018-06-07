package controllers;

import com.google.inject.Inject;
import controllers.api.v1.Users;
import models.User;
import org.pac4j.play.java.RequiresAuthentication;
import org.pac4j.play.java.UserProfileController;
import play.Logger;
import play.mvc.*;
import security.MrBlackUserProfile;
import to.guest.GuestReservationTicket;
import util.ReservationsHelper;

import java.util.UUID;

public class Application extends UserProfileController<MrBlackUserProfile> {

	@Inject
	private ReservationsHelper reservationsHelper;

	public Result logout() {
		response().discardCookie("api_key");
		return redirect(org.pac4j.play.routes.ApplicationLogoutController.logout());
	}

	public Result reservation(String token) {
		GuestReservationTicket ticket;
		try {
			ticket = reservationsHelper.getGuestReservationTicket(token);
		} catch (Exception e) {
			Logger.warn("Can't fetch reservation by token=" + token, e);
			return internalServerError();
		}

		if (ticket == null) {
			Logger.info("Reservation by id=" + token + " not found");
			return notFound();
		}

		return ok(views.html.reservation.render(ticket));
	}

	public Result swagger() {
		return ok(views.html.swagger.render());
	}

	@RequiresAuthentication(clientName = "FormClient,IndirectCookieClient")
	public Result index() {
		boolean hasApiKeyCookie = isHasApiKeyCookie();
		if (hasApiKeyCookie) {
			return ok(views.html.index.render());
		} else {
			return redirect(controllers.routes.Application.auth());
		}
    }

	@RequiresAuthentication(clientName = "FormClient,IndirectCookieClient")
	public Result venue(String id) {
		boolean hasApiKeyCookie = isHasApiKeyCookie();
		if (hasApiKeyCookie) {
			return ok(views.html.index.render());
		} else {
			return redirect(controllers.routes.Application.auth());
		}
    }

	@RequiresAuthentication(clientName = "FormClient")
	public Result auth() {
		MrBlackUserProfile profile = getUserProfile();
		User user = profile.getUser();

		boolean hasApiKeyCookie = isHasApiKeyCookie();
		if (!hasApiKeyCookie) {
			final String newApiKey;
			try {
				newApiKey = UUID.randomUUID().toString();
				user.addApiKey(newApiKey);
				if (!Users.isDemoUser(user.getPhoneNumber())) user.setSecurityCode(null);
				user.update();
			} catch (Exception e) {
				Logger.warn("Can't create session", e);
				return redirect(controllers.routes.Application.login());
			}

			final String domain = ctx()._requestHeader().domain();
			response().setCookie("api_key", newApiKey, 3153600, "/", domain, false, true);
		}

		return redirect(controllers.routes.Application.index());
	}

	private boolean isHasApiKeyCookie() {
		final Http.Cookie apiKey = request().cookies().get("api_key");
		return apiKey != null;
	}

	public Result login() {
        return ok(views.html.login.render());
    }

	public Result untrail(String path) {
		return movedPermanently("/" + path);
	}
}
