package to;

import io.swagger.annotations.ApiModelProperty;
import models.PayInfo;
import models.User;
import org.apache.commons.lang3.StringUtils;
import util.PhoneHelper;

import java.time.LocalDate;

/**
 * Created by arkady on 28/06/16.
 */
public class PayInfoTO {
	private Long id;
	private Long userId;
	@ApiModelProperty(required = true)
	private String firstName;
	@ApiModelProperty(required = true)
	private String lastName;
	@ApiModelProperty(required = true)
	private String address;
	@ApiModelProperty(required = true)
	private String city;
	private String state;
	@ApiModelProperty(required = true)
	private String country;
	@ApiModelProperty(required = true)
	private String postalCode;
	private String email;
	private String companyName;
	private String phoneNumber;
	@ApiModelProperty(required = true)
	private String dateOfBirth;

	public PayInfoTO() {
	}

	public PayInfoTO(PayInfo payInfo) {
		if (payInfo == null) return;
		this.id = payInfo.getId();
		this.firstName = payInfo.getFirstName();
		this.lastName = payInfo.getLastName();
		this.address = payInfo.getAddress();
		this.city = payInfo.getCity();
		this.state = payInfo.getState();
		this.country = payInfo.getCountry();
		this.postalCode = payInfo.getPostalCode();
		this.email = payInfo.getEmail();
		this.companyName = payInfo.getCompanyName();
		this.phoneNumber = payInfo.getPhoneNumber();
		if (payInfo.getDateOfBirth() != null) {
			this.dateOfBirth = payInfo.getDateOfBirth().toString();
		}
		if (payInfo.getUser() != null) {
			this.userId = payInfo.getUser().getId();
		}
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public PayInfo toNewPayInfo() throws NumberFormatException {
		PayInfo payInfo = new PayInfo();
		payInfo.setId(this.id);
		payInfo.setFirstName(this.firstName);
		payInfo.setLastName(this.lastName);
		payInfo.setAddress(this.address);
		payInfo.setCity(this.city);
		payInfo.setState(this.state);
		payInfo.setCountry(this.country);
		payInfo.setPostalCode(this.postalCode);
		payInfo.setEmail(this.email);
		payInfo.setCompanyName(this.companyName);
		payInfo.setPhoneNumber(PhoneHelper.normalizePhoneNumber(this.phoneNumber));
		if (StringUtils.isNotEmpty(this.dateOfBirth)) {
			payInfo.setDateOfBirth(LocalDate.parse(this.dateOfBirth));
		}
		if (this.userId != null) {
			payInfo.setUser(new User(this.userId));
		}
		return payInfo;
	}
}
