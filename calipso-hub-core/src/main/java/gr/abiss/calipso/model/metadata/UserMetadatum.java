package gr.abiss.calipso.model.metadata;

import gr.abiss.calipso.model.entities.AbstractAuditabeMetadatum;
import gr.abiss.calipso.model.User;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

@Entity
@Table(name = "user_metadatum")
@Inheritance(strategy = InheritanceType.JOINED)
public class UserMetadatum extends AbstractAuditabeMetadatum<User, User> {

	private static final long serialVersionUID = 5885643690209874078L;

	public UserMetadatum() {
		super(null, null, null);
	}

	public UserMetadatum(String predicate, String object) {
		super(predicate, object);
	}

	public UserMetadatum(User subject, String predicate, String object) {
		super(subject, predicate, object);
	}

}