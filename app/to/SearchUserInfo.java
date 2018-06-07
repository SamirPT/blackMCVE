package to;

import models.User;

/**
 * Created by arkady on 17/03/16.
 */
public class SearchUserInfo {
	private Long id;
	private String name;
	private String phone;

	public SearchUserInfo() {
	}

	public SearchUserInfo(Long id, String name, String phone) {
		this.id = id;
		this.name = name;
		this.phone = phone;
	}

	public SearchUserInfo(User user) {
		this.id = user.getId();
		this.name = user.getFullName();
		this.phone = user.getPhoneNumber();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
}
