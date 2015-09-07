package gr.abiss.calipso.model.geography;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import gr.abiss.calipso.model.User;
import gr.abiss.calipso.model.entities.AbstractAuditable;

/**
 * Represents regions within a country.
 */
@Entity
public class LocalRegion<P extends LocalRegion> 
	extends AbstractAuditable<User> {

	private static final long serialVersionUID = 1735385884991197359L;
	
	private Country country;

	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}
}
