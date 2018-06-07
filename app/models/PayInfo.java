package models;

import com.avaje.ebean.Model;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by arkady on 28/06/16.
 */
@Entity
public class PayInfo extends Model {

	@Id
	private Long id;
	private String firstName;
	private String lastName;
	private String address;
	private String city;
	private String state;
	private String country;
	private String postalCode;
	private String email;
	private String companyName;
	private String phoneNumber;
	private LocalDate dateOfBirth;

	@ManyToMany(fetch = FetchType.LAZY)
	private List<Reservation> payee = new ArrayList<>();

	@OneToOne(fetch = FetchType.LAZY)
	private User user;

	public static Finder<Long, PayInfo> finder = new Finder<>(PayInfo.class);

	public PayInfo() {
	}

	public PayInfo(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<Reservation> getPayee() {
		return payee;
	}

	public void setPayee(List<Reservation> payee) {
		this.payee = payee;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
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

	public LocalDate getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(LocalDate dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	@Override
	public String toString() {
		return "PayInfo{" +
				"id=" + id +
				", firstName='" + firstName + '\'' +
				", lastName='" + lastName + '\'' +
				", address='" + address + '\'' +
				", city='" + city + '\'' +
				", state='" + state + '\'' +
				", country='" + country + '\'' +
				", postalCode='" + postalCode + '\'' +
				", email='" + email + '\'' +
				", companyName='" + companyName + '\'' +
				", phoneNumber='" + phoneNumber + '\'' +
				", dateOfBirth=" + dateOfBirth +
				", payee=" + payee +
				", user=" + user +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		PayInfo payInfo = (PayInfo) o;

		if (id != null ? !id.equals(payInfo.id) : payInfo.id != null) return false;
		if (firstName != null ? !firstName.equals(payInfo.firstName) : payInfo.firstName != null) return false;
		if (lastName != null ? !lastName.equals(payInfo.lastName) : payInfo.lastName != null) return false;
		if (address != null ? !address.equals(payInfo.address) : payInfo.address != null) return false;
		if (city != null ? !city.equals(payInfo.city) : payInfo.city != null) return false;
		if (state != null ? !state.equals(payInfo.state) : payInfo.state != null) return false;
		if (country != null ? !country.equals(payInfo.country) : payInfo.country != null) return false;
		if (postalCode != null ? !postalCode.equals(payInfo.postalCode) : payInfo.postalCode != null) return false;
		if (email != null ? !email.equals(payInfo.email) : payInfo.email != null) return false;
		if (companyName != null ? !companyName.equals(payInfo.companyName) : payInfo.companyName != null) return false;
		if (phoneNumber != null ? !phoneNumber.equals(payInfo.phoneNumber) : payInfo.phoneNumber != null) return false;
		if (dateOfBirth != null ? !dateOfBirth.equals(payInfo.dateOfBirth) : payInfo.dateOfBirth != null) return false;
		if (payee != null ? !payee.equals(payInfo.payee) : payInfo.payee != null) return false;
		return user != null ? user.equals(payInfo.user) : payInfo.user == null;

	}

	@Override
	public int hashCode() {
		int result = id != null ? id.hashCode() : 0;
		result = 31 * result + (firstName != null ? firstName.hashCode() : 0);
		result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
		result = 31 * result + (address != null ? address.hashCode() : 0);
		result = 31 * result + (city != null ? city.hashCode() : 0);
		result = 31 * result + (state != null ? state.hashCode() : 0);
		result = 31 * result + (country != null ? country.hashCode() : 0);
		result = 31 * result + (postalCode != null ? postalCode.hashCode() : 0);
		result = 31 * result + (email != null ? email.hashCode() : 0);
		result = 31 * result + (companyName != null ? companyName.hashCode() : 0);
		result = 31 * result + (phoneNumber != null ? phoneNumber.hashCode() : 0);
		result = 31 * result + (dateOfBirth != null ? dateOfBirth.hashCode() : 0);
		result = 31 * result + (payee != null ? payee.hashCode() : 0);
		result = 31 * result + (user != null ? user.hashCode() : 0);
		return result;
	}
}
