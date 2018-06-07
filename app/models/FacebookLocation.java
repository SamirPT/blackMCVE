package models;

/**
 * Created by arkady on 10/02/16.
 */

import com.avaje.ebean.Model;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "facebook_location")
public class FacebookLocation extends Model {

	@Id
	@ApiModelProperty(required = false, hidden = true)
	Long id;
	@ApiModelProperty(required = false)
	String city;
	@ApiModelProperty(required = false)
	String country;
	@ApiModelProperty(required = false)
	Float latitude;
	@ApiModelProperty(required = false)
	Float longitude;
	@ApiModelProperty(required = false)
	String name;
	@ApiModelProperty(required = false)
	String region;
	@ApiModelProperty(required = false)
	String state;
	@ApiModelProperty(required = false)
	String street;
	@ApiModelProperty(required = false)
	String zip;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public Float getLatitude() {
		return latitude;
	}

	public void setLatitude(Float latitude) {
		this.latitude = latitude;
	}

	public Float getLongitude() {
		return longitude;
	}

	public void setLongitude(Float longitude) {
		this.longitude = longitude;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}
}
