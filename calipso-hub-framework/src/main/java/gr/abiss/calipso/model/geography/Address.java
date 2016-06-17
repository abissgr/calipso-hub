package gr.abiss.calipso.model.geography;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;

import gr.abiss.calipso.model.User;
import gr.abiss.calipso.model.entities.AbstractAuditable;


/**
 * An entity topersist postal addresses. 
 */
@Entity
@Table(name="postal_address")
public class Address extends AbstractAuditable<User> {
	
	private static final long serialVersionUID = 1L;

	private String address;

	private String address2;

	private String district;

	private String phone;

	@Column(name="postal_code")
	private String postalCode;

	@JsonProperty("lon")
	private Double longitude;
	
	@JsonProperty("lat")
	private Double latitude;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "city_id", referencedColumnName = "id", nullable = false)
	private LocalRegion city;


	public Address() {
	}


	public String getAddress() {
		return address;
	}


	public void setAddress(String address) {
		this.address = address;
	}


	public String getAddress2() {
		return address2;
	}


	public void setAddress2(String address2) {
		this.address2 = address2;
	}


	public String getDistrict() {
		return district;
	}


	public void setDistrict(String district) {
		this.district = district;
	}


	public String getPhone() {
		return phone;
	}


	public void setPhone(String phone) {
		this.phone = phone;
	}


	public String getPostalCode() {
		return postalCode;
	}


	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}


	public Double getLongitude() {
		return longitude;
	}


	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}


	public Double getLatitude() {
		return latitude;
	}


	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}


	public LocalRegion getCity() {
		return city;
	}


	public void setCity(LocalRegion city) {
		this.city = city;
	}
	
	

}