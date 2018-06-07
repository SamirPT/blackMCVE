package security;

import org.pac4j.core.context.WebContext;
import org.pac4j.http.credentials.TokenCredentials;
import org.pac4j.http.credentials.extractor.Extractor;
import play.mvc.Http;

/**
 * Created by arkady on 31/05/16.
 */
public class CookieExtractor implements Extractor<TokenCredentials> {

	private final String cookieName;

	private final String clientName;

	public CookieExtractor(final String cookieName, final String clientName) {
		this.cookieName = cookieName;
		this.clientName = clientName;
	}

	@Override
	public TokenCredentials extract(final WebContext context) {
		final Http.Cookies col = Http.Context.current().request().cookies();
		for (final Http.Cookie c : col) {
			if (c.name().equals(this.cookieName)) {
				return new TokenCredentials(c.value(), clientName);
			}
		}
		return null;
	}
}
