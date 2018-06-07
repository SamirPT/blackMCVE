package security;

import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.http.HttpActionAdapter;
import org.pac4j.play.PlayWebContext;

import play.Logger;
import play.libs.Json;
import to.ErrorMessage;

import static play.mvc.Results.*;

/**
 * Created by arkady on 26/02/16.
 */
public class CommonHttpActionAdapter implements HttpActionAdapter {

	@Override
	public Object adapt(int code, WebContext context) {
		final PlayWebContext webContext = (PlayWebContext) context;
		if (code == HttpConstants.UNAUTHORIZED) {
			return unauthorized(Json.toJson(new ErrorMessage("Authentication required")));
		} else if (code == HttpConstants.FORBIDDEN) {
			return forbidden(ErrorMessage.getJsonForbiddenMessage());
		} else if (code == HttpConstants.TEMP_REDIRECT) {
			return redirect(webContext.getResponseLocation());
		} else if (code == HttpConstants.OK) {
			final String content = webContext.getResponseContent();
			return ok(content).as(HttpConstants.HTML_CONTENT_TYPE);
		}
		final String message = "Unsupported HTTP action: " + code;
		Logger.warn(message);
		throw new TechnicalException(message);
	}
}
