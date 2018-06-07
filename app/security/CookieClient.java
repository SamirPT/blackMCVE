package security;

import org.pac4j.core.client.ClientType;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.http.client.direct.DirectHttpClient;
import org.pac4j.http.credentials.TokenCredentials;
import org.pac4j.http.credentials.authenticator.TokenAuthenticator;
import org.pac4j.http.profile.creator.ProfileCreator;

/**
 * Allows direct authentication based on a cookie.
 * @author Arkady Karev
 */
public class CookieClient extends DirectHttpClient<TokenCredentials> {
	private String cookieName;


	public CookieClient() {
	}

	public CookieClient(final TokenAuthenticator cookieAuthenticator) {
		setAuthenticator(cookieAuthenticator);
	}

	public CookieClient(final TokenAuthenticator cookieAuthenticator,
						final ProfileCreator profileCreator) {
		setAuthenticator(cookieAuthenticator);
		setProfileCreator(profileCreator);
	}

	public String getCookieName() {
		return cookieName;
	}

	public void setCookieName(String cookieName) {
		this.cookieName = cookieName;
	}

	@Override
	protected void internalInit(final WebContext context) {
		CommonHelper.assertNotBlank("cookieName", this.cookieName);
		extractor = new CookieExtractor(this.cookieName, getName());
		super.internalInit(context);

	}

	@Override
	protected CookieClient newClient() {
		final CookieClient newClient = new CookieClient();
		newClient.setCookieName(this.cookieName);
		return newClient;
	}

	@Override
	public ClientType getClientType() {
		return ClientType.COOKIE_BASED;
	}
}
