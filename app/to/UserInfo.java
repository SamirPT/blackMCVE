package to;

import io.swagger.annotations.ApiModelProperty;
import models.FacebookInfo;
import models.PromotionCompany;
import models.User;
import models.VenueRole;
import util.PhoneHelper;

import javax.persistence.Column;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by arkady on 12/02/16.
 */
public class UserInfo {

	@ApiModelProperty()
	private Long id;

	@ApiModelProperty()
	private String fullName;

	@ApiModelProperty()
	private String email;

	@ApiModelProperty()
	private String userpic;

	@ApiModelProperty(required = true)
	@Column(unique=true)
	private String phoneNumber;

	@ApiModelProperty(example = "1990-01-01")
	private String birthday;

	@ApiModelProperty()
	private List<VenueRole> preferredRoles;

	@ApiModelProperty(required = true, readOnly = true)
	private boolean hasFacebookProfile;

	private FacebookInfo facebookInfo;
	private PromotionCompany promotionCompany;
	private Boolean isAdmin;

	public UserInfo() {
	}

	public UserInfo(User user) {
		if (user == null) return;

		this.setId(user.getId());
		if (user.getBirthday() != null) this.setBirthday(user.getBirthday().toString());
		this.setEmail(user.getEmail());
		this.setFullName(user.getFullName());
		this.setPhoneNumber(user.getPhoneNumber());
		this.setUserpic(user.getUserpic());
		this.setPreferredRoles(user.getPreferredRoles());
		this.setHasFacebookProfile(user.getFacebookInfo() != null);
		this.setIsAdmin(Boolean.TRUE.equals(user.isAdmin()));
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getEmail() {
		return email;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public List<VenueRole> getPreferredRoles() {
		return preferredRoles;
	}

	public void setPreferredRoles(List<VenueRole> preferredRoles) {
		this.preferredRoles = preferredRoles;
	}

	public String getUserpic() {
		return userpic;
	}

	public void setUserpic(String userpic) {
		this.userpic = userpic;
	}

	public void setHasFacebookProfile(boolean hasFacebookProfile) {
		this.hasFacebookProfile = hasFacebookProfile;
	}

	public boolean isHasFacebookProfile() {
		return hasFacebookProfile;
	}

	public FacebookInfo getFacebookInfo() {
		return facebookInfo;
	}

	public void setFacebookInfo(FacebookInfo facebookInfo) {
		this.facebookInfo = facebookInfo;
	}

	public void setPromotionCompany(PromotionCompany promotionCompany) {
		this.promotionCompany = promotionCompany;
	}

	public PromotionCompany getPromotionCompany() {
		return promotionCompany;
	}

	public User toUser() {
		User user = new User();
		user.setFacebookInfo(facebookInfo);
		user.setBirthday(LocalDate.parse(birthday));
		user.setEmail(email);
		user.setFullName(fullName);
		user.setId(id);
		user.setPromotionCompany(promotionCompany);
		user.setPhoneNumber(PhoneHelper.normalizePhoneNumber(phoneNumber));
		if (preferredRoles != null) {
			user.setPreferredRoles(preferredRoles);
		} else {
			user.setPreferredRoles(new ArrayList<>());
		}
		user.setUserpic(userpic);
		return user;
	}

	public void setIsAdmin(Boolean isAdmin) {
		this.isAdmin = isAdmin;
	}

	public Boolean getIsAdmin() {
		return isAdmin;
	}
}
