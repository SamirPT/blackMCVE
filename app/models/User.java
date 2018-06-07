package models;

import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.DbJsonB;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import io.jsonwebtoken.lang.Collections;
import io.swagger.annotations.ApiModelProperty;
import play.libs.Json;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by arkady on 15/01/16.
 */
@Entity
@Table(name = "userr")
public class User extends Model {

	@Id
	@ApiModelProperty(hidden = true)
	private Long id;

	@ApiModelProperty()
	private String fullName;

	@ApiModelProperty()
	private String email;

	@ApiModelProperty()
	private String userpic;

	@ApiModelProperty(required = true)
	@Column(unique=true, nullable = false)
	private String phoneNumber;

	@ApiModelProperty(example = "1990-01-01")
	private LocalDate birthday;

	@ApiModelProperty(hidden = true)
	private String securityCode;

	@ApiModelProperty(hidden = true)
	@DbJsonB
	private JsonNode apiKeys;

	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private FacebookInfo facebookInfo;

	@Column(columnDefinition = "boolean default true")
	private Boolean postOnMyBehalf;
	@Column(columnDefinition = "boolean default true")
	private Boolean rsvpOnMyBehalf;
	@Column(columnDefinition = "boolean default true")
	private Boolean checkInOnMyBehalf;
	@Column(columnDefinition = "boolean default true")
	private Boolean smsMessages;
	@Column(columnDefinition = "boolean default true")
	private Boolean inAppNotification;

	@Column(columnDefinition = "boolean default true")
	private Boolean guestPostOnMyBehalf;
	@Column(columnDefinition = "boolean default true")
	private Boolean guestRsvpOnMyBehalf;
	@Column(columnDefinition = "boolean default true")
	private Boolean guestCheckInOnMyBehalf;
	@Column(columnDefinition = "boolean default true")
	private Boolean guestSmsMessages;
	@Column(columnDefinition = "boolean default true")
	private Boolean guestInAppNotification;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "user")
	private List<Feedback> feedbackList = new ArrayList<>();

	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "user")
	private PayInfo payInfo;

	@DbJsonB
	@ApiModelProperty
	private JsonNode preferredRoles;

	@Column(columnDefinition = "boolean default false")
	@ApiModelProperty(hidden = true)
	private Boolean isAdmin;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "promotion_company_id", referencedColumnName = "id")
	private PromotionCompany promotionCompany;

	@Column(columnDefinition = "varchar(1000)")
	private String IOSToken;

	public static Finder<Long, User> finder = new Finder<>(User.class);

	public User() {
	}

	public User(Long id) {
		this.id = id;
	}

	private List<String> getApiKeys() {
		List<String> result;
		if (apiKeys != null) {
			result = Arrays.asList(Json.fromJson(apiKeys, String[].class));
		} else {
			result = new ArrayList<>();
		}
		return result;
	}

	private void setApiKeys(List<String> apiKeys) {
		if (apiKeys == null) apiKeys = new ArrayList<>();
		this.apiKeys = Json.toJson(apiKeys);
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

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public LocalDate getBirthday() {
		return birthday;
	}

	public void setBirthday(LocalDate birthday) {
		this.birthday = birthday;
	}

	public FacebookInfo getFacebookInfo() {
		return facebookInfo;
	}

	public void setFacebookInfo(FacebookInfo facebookInfo) {
		this.facebookInfo = facebookInfo;
	}

	public String getSecurityCode() {
		return securityCode;
	}

	public void setSecurityCode(String securityCode) {
		this.securityCode = securityCode;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUserpic() {
		return userpic;
	}

	public void setUserpic(String userpic) {
		this.userpic = userpic;
	}

	public List<Feedback> getFeedbackList() {
		return feedbackList;
	}

	public void setFeedbackList(List<Feedback> feedbackList) {
		this.feedbackList = feedbackList;
	}

	public List<VenueRole> getPreferredRoles() {
		List<VenueRole> result = null;
		if (preferredRoles != null) {
			result = Collections.arrayToList(Json.fromJson(preferredRoles, VenueRole[].class));
		}
		return result;
	}

	public void setPreferredRoles(List<VenueRole> preferredRoles) {
		if (preferredRoles != null) {
			this.preferredRoles = Json.toJson(preferredRoles);
		}
	}

	public Boolean isAdmin() {
		return isAdmin;
	}

	public void setAdmin(Boolean isAdmin) {
		this.isAdmin = isAdmin;
	}

	public PromotionCompany getPromotionCompany() {
		return promotionCompany;
	}

	public void setPromotionCompany(PromotionCompany promotionCompany) {
		this.promotionCompany = promotionCompany;
	}

	public Boolean getPostOnMyBehalf() {
		return postOnMyBehalf;
	}

	public void setPostOnMyBehalf(Boolean postOnMyBehalf) {
		this.postOnMyBehalf = postOnMyBehalf;
	}

	public Boolean getRsvpOnMyBehalf() {
		return rsvpOnMyBehalf;
	}

	public void setRsvpOnMyBehalf(Boolean rsvpOnMyBehalf) {
		this.rsvpOnMyBehalf = rsvpOnMyBehalf;
	}

	public Boolean getCheckInOnMyBehalf() {
		return checkInOnMyBehalf;
	}

	public void setCheckInOnMyBehalf(Boolean checkInOnMyBehalf) {
		this.checkInOnMyBehalf = checkInOnMyBehalf;
	}

	public Boolean getSmsMessages() {
		return smsMessages;
	}

	public void setSmsMessages(Boolean smsMessages) {
		this.smsMessages = smsMessages;
	}

	public Boolean getInAppNotification() {
		return inAppNotification;
	}

	public void setInAppNotification(Boolean inAppNotification) {
		this.inAppNotification = inAppNotification;
	}

	public Boolean getGuestPostOnMyBehalf() {
		return guestPostOnMyBehalf;
	}

	public void setGuestPostOnMyBehalf(Boolean guestPostOnMyBehalf) {
		this.guestPostOnMyBehalf = guestPostOnMyBehalf;
	}

	public Boolean getGuestRsvpOnMyBehalf() {
		return guestRsvpOnMyBehalf;
	}

	public void setGuestRsvpOnMyBehalf(Boolean guestRsvpOnMyBehalf) {
		this.guestRsvpOnMyBehalf = guestRsvpOnMyBehalf;
	}

	public Boolean getGuestCheckInOnMyBehalf() {
		return guestCheckInOnMyBehalf;
	}

	public void setGuestCheckInOnMyBehalf(Boolean guestCheckInOnMyBehalf) {
		this.guestCheckInOnMyBehalf = guestCheckInOnMyBehalf;
	}

	public Boolean getGuestSmsMessages() {
		return guestSmsMessages;
	}

	public void setGuestSmsMessages(Boolean guestSmsMessages) {
		this.guestSmsMessages = guestSmsMessages;
	}

	public Boolean getGuestInAppNotification() {
		return guestInAppNotification;
	}

	public void setGuestInAppNotification(Boolean guestInAppNotification) {
		this.guestInAppNotification = guestInAppNotification;
	}

	public PayInfo getPayInfo() {
		return payInfo;
	}

	public void setPayInfo(PayInfo payInfo) {
		this.payInfo = payInfo;
	}

	@Override
	public String toString() {
		return "User{" +
				"id=" + id +
				", fullName='" + fullName + '\'' +
				", email='" + email + '\'' +
				", userpic='" + userpic + '\'' +
				", phoneNumber='" + phoneNumber + '\'' +
				", birthday=" + birthday +
				", securityCode='" + securityCode + '\'' +
				", facebookInfo=" + facebookInfo +
				", feedbackList=" + feedbackList +
				", preferredRoles=" + preferredRoles +
				'}';
	}

	@JsonIgnore
	public void addApiKey(String newApiKey) {
		List<String> keyList = new ArrayList<>(getApiKeys());
		keyList.add(newApiKey);
		setApiKeys(keyList);
	}

	public void setIOSToken(String IOSToken) {
		this.IOSToken = IOSToken;
	}

	public String getIOSToken() {
		return IOSToken;
	}
}
