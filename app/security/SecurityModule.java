package security;

import com.google.inject.AbstractModule;
import models.User;
import org.pac4j.core.authorization.RequireAnyRoleAuthorizer;
import org.pac4j.core.client.Clients;
import org.pac4j.core.config.Config;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.http.client.direct.ParameterClient;
import org.pac4j.http.client.indirect.FormClient;
import org.pac4j.http.credentials.authenticator.TokenAuthenticator;
import org.pac4j.http.credentials.authenticator.UsernamePasswordAuthenticator;
import org.pac4j.play.ApplicationLogoutController;
import play.Logger;
import util.PhoneHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arkady on 22/01/16.
 */
public class SecurityModule extends AbstractModule {

	@Override
	protected void configure() {
		final TokenAuthenticator tokenAuthenticator = credentials -> {
			if (credentials == null) {
				throw new CredentialsException("credentials must not be null");
			}
			if (CommonHelper.isBlank(credentials.getToken())) {
				throw new CredentialsException("token must not be blank");
			}
			final String apiKey = credentials.getToken();

			User user;
			try {
				user = User.finder.where().raw("api_keys ?? '" + apiKey + "'").findUnique();
			} catch (Exception e) {
				Logger.warn("Can't find user by api_key", e);
				throw e;
			}

			if (user == null) {
				throw new CredentialsException("Invalid token");
			}
			final MrBlackUserProfile profile = getMrBlackUserProfile(user);
			credentials.setUserProfile(profile);
		};

		UsernamePasswordAuthenticator usernamePasswordAuthenticator = credentials -> {
			if (credentials == null) {
				throw new CredentialsException("No credential");
			}
			String phoneNumber = credentials.getUsername();
			String securityCode = credentials.getPassword();
			if (CommonHelper.isBlank(phoneNumber)) {
				throw new CredentialsException("Phone number cannot be blank");
			}
			if (CommonHelper.isBlank(securityCode)) {
				throw new CredentialsException("Security code cannot be blank");
			}

			User user;
			try {
				user = User.finder.where()
						.eq("securityCode", securityCode)
						.eq("phoneNumber", PhoneHelper.normalizePhoneNumber(phoneNumber))
						.findUnique();
			} catch (NumberFormatException e) {
				throw new CredentialsException("Invalid phone number");
			} catch (Exception e) {
				Logger.warn("Can't find user by securityCode", e);
				throw e;
			}

			if (user == null) {
				throw new CredentialsException("Unknown phone number or invalid security code");
			}
			final MrBlackUserProfile profile = getMrBlackUserProfile(user);
			credentials.setUserProfile(profile);
		};

		final FormClient formClient = new FormClient("/signin", usernamePasswordAuthenticator);

		CookieClient cookieClient = new CookieClient(tokenAuthenticator);
		cookieClient.setName("IndirectCookieClient");
		cookieClient.setCookieName("api_key");

		ParameterClient parameterClient = new ParameterClient("api_key", tokenAuthenticator);
		parameterClient.setSupportGetRequest(true);

		Clients clients = new Clients("/callback", parameterClient, cookieClient, formClient);
		Config config = new Config(clients);
		config.addAuthorizer("admin", new RequireAnyRoleAuthorizer("ROLE_ADMIN"));
		config.setHttpActionAdapter(new CommonHttpActionAdapter());
		bind(Config.class).toInstance(config);

		final ApplicationLogoutController logoutController = new ApplicationLogoutController();
		logoutController.setDefaultUrl("/");
		bind(ApplicationLogoutController.class).toInstance(logoutController);
	}

	private MrBlackUserProfile getMrBlackUserProfile(User user) {
		final MrBlackUserProfile profile = new MrBlackUserProfile();
		profile.setUser(user);
		List<String> systemRoles = new ArrayList<>();
		Logger.info("Some user info " + user.toString());
		if (user.isAdmin() != null && user.isAdmin()) systemRoles.add("ROLE_ADMIN");
		profile.setRoles(systemRoles);
		return profile;
	}
}