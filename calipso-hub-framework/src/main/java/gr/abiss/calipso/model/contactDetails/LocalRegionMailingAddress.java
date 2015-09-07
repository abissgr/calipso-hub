package gr.abiss.calipso.model.contactDetails;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;

import gr.abiss.calipso.model.contactDetails.base.AbstractLocalRegionMailingAddress;

@Entity
public class LocalRegionMailingAddress extends AbstractLocalRegionMailingAddress{

	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", unique = true)
	private String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	
}
