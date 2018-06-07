import akka.actor.Cancellable;
import com.restfb.exception.FacebookOAuthException;
import models.FacebookInfo;
import models.User;
import modules.fb.FB;
import modules.fb.FBImpl;
import play.Application;
import play.GlobalSettings;
import play.Logger;
import play.libs.Akka;
import play.libs.F;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import scala.concurrent.duration.Duration;
import to.ErrorMessage;

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by arkady on 18/04/16.
 */
public class Global extends GlobalSettings {

	@Inject
	FB fb;

	private Cancellable scheduler;

	@Override
	public void onStart(Application app) {
		super.onStart(app);

		scheduler = Akka.system().scheduler().schedule(Duration.Zero(), Duration.create(1, TimeUnit.DAYS), () -> {
			List<User> users = User.finder.fetch("facebookInfo").where().isNotNull("facebookInfo").findList();

			if (users != null) {
				if (fb == null) {
					fb = app.getWrappedApplication().injector().instanceOf(FBImpl.class);
				}

				Logger.info("Found " + users.size() + " user(s) connected to Facebook account. Starting to update Facebook tokens...");
				users.forEach(user -> {
					final FacebookInfo facebookInfo = user.getFacebookInfo();
					boolean facebookTokenExpired = false;
					int numberOfFriends = 0;
					try {
						numberOfFriends = fb.getNumberOfFriends(facebookInfo.getToken());
					} catch (FacebookOAuthException e) {
						facebookTokenExpired = true;
						Logger.warn("Found facebook info with expired token. Deleting FacebookInfo.. UserId=" + user.getId());
					} catch (Exception e) {
						Logger.warn("Something wrong with attempt to refresh Facebook token for user id:" + user.getId() +
								". Would you like to delete his Facebook profile from DB?", e);
					}

					try {
						if (facebookTokenExpired) {
							user.setFacebookInfo(null);
							user.update();
							facebookInfo.delete();
						} else {
							final Short newFriendsNumber = Integer.valueOf(numberOfFriends).shortValue();
							if (!newFriendsNumber.equals(facebookInfo.getFriendsNumber())) {
								facebookInfo.setFriendsNumber(newFriendsNumber);
								facebookInfo.update();
							}
						}
					} catch (Exception e) {
						Logger.warn("Can't update user's data", e);
					}
				});
			} else {
				Logger.info("Still no users with Facebook account :(");
			}
		}, Akka.system().dispatcher());
	}

	@Override
	public void onStop(Application app) {
		super.onStop(app);
		if (scheduler != null) {
			scheduler.cancel();
		}
	}

	@Override
	public F.Promise<Result> onHandlerNotFound(Http.RequestHeader request) {
		if (request.path().contains("/api/v"))
			return F.Promise.pure(Results.notFound(Json.toJson(new ErrorMessage("Not found"))));
		else
			return super.onHandlerNotFound(request);
	}

	@Override
	public F.Promise<Result> onBadRequest(Http.RequestHeader request, String error) {
		if (request.path().contains("/api/v"))
			return F.Promise.pure(Results.badRequest(Json.toJson(new ErrorMessage(error))));
		else
			return super.onBadRequest(request, error);
	}

	@Override
	public F.Promise<Result> onError(Http.RequestHeader request, Throwable t) {
		if (request.path().contains("/api/v"))
			return F.Promise.pure(Results.internalServerError(Json.toJson(new ErrorMessage(t.getMessage()))));
		else
			return super.onError(request, t);
	}
}
