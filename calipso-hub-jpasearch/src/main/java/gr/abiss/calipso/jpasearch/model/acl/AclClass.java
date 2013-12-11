package gr.abiss.calipso.jpasearch.model.acl;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.domain.Persistable;

@Entity
@Table(name = "acl_class")
public class AclClass implements Persistable<Long> {

	private static final long serialVersionUID = -8538893700776925998L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", unique = true)
	private Long id;

	@Column(name = "class", unique = true)
	private String className;

	public AclClass() {
		super();
	}

	public AclClass(Long id, String clazz) {
		super();
		this.id = id;
		this.className = clazz;
	}

	@Override
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getClassName() {
		return className;
	}

	public Class getClassObject() {
		Class clazz = null;
		try {
			clazz = Class.forName(className);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return clazz;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	@Override
	public boolean isNew() {
		return this.getId() == null;
	}

}
