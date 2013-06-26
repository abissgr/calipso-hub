package gr.abiss.calipso.model.acl;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.domain.Persistable;

@Entity
@Table(name = "access_mode")
public class AccessMode implements Persistable<Short> {

	private static final long serialVersionUID = 6007713619677213125L;

	/**
	 * The primary key
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Short id;

	@Column(name = "name", unique = true, nullable = false)
	private String name;

	@Column(name = "weight", unique = true, nullable = false)
	private Short weight;

	/**
	 * @see org.springframework.data.domain.Persistable#getId()
	 */
	@Override
	public Short getId() {
		return this.id;
	}

	/**
	 * @see org.springframework.data.domain.Persistable#isNew()
	 */
	@Override
	public boolean isNew() {

		return null == getId();
	}
	// READ, WRITE, WRITE_MANDATORY, WRITE_MANDATORY_IF_EMPTY, APPEND,
	// APPEND_MANDATORY, APPEND_MANDATORY_IF_EMPTY, CONTROL;
}
