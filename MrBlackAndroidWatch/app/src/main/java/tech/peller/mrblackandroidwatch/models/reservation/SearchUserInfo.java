package tech.peller.mrblackandroidwatch.models.reservation;

import java.io.Serializable;

import io.realm.RealmObject;

/**
 * Created by arkady on 17/03/16.
 */
public class SearchUserInfo extends RealmObject implements Serializable {
	private Long id;
	private Long guestInfoId;
	private String name;
	private String phone;
	private String birthday;
	private String email;


	public SearchUserInfo() {
	}

	public SearchUserInfo(Long id, String name, String phone) {
		this.id = id;
		this.name = name;
		this.phone = phone;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getGuestInfoId() {
		return guestInfoId;
	}

	public void setGuestInfoId(Long guestInfoId) {
		this.guestInfoId = guestInfoId;
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

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
