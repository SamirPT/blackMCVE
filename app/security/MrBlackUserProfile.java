package security;

import models.User;
import org.pac4j.http.profile.HttpProfile;

import java.util.List;

/**
 * Created by arkady on 26/02/16.
 */
public class MrBlackUserProfile extends HttpProfile {
	private User user;
	private List<String> roles;

	public MrBlackUserProfile() {
	}

	public MrBlackUserProfile(User user) {
		this.user = user;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}
}
