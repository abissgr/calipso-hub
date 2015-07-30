package gr.abiss.calipso.model.base;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import gr.abiss.calipso.model.User;
import gr.abiss.calipso.model.entities.AbstractAuditable;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Transient;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;


/**
 * A base class for auditable resource entities: files, folders, categories etc.
 */
@MappedSuperclass
public abstract class AuditableResource<T extends AuditableResource<T>> extends AbstractAuditable<User> {

	private static final String PATH_SEPARATOR = "/";

	/**
	 * The HTTP URL of the resource, excluding the protocol, domain and port. Starts with a slash. 
	 */
	@Column(name = "name", length = 500, nullable = false)
	private String name;

	/**
	 * The HTTP URL of the resource, excluding the protocol, domain and port. Starts with a slash. 
	 */
	@Column(name = "path", length = 1500, nullable = false)
	private String path;
	
	/**
	 * The HTTP URL of the resource, excluding the protocol, domain and port. Starts with a slash. 
	 */
	@Column(name = "path_level", nullable = false)
	private Short pathLevel;

	@JsonIgnore
	@ManyToOne(/* cascade=CascadeType.ALL, */fetch = FetchType.EAGER)
	@JoinColumn(name = "same_as", referencedColumnName = "id", nullable = true)
	private T sameAs;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "parent", referencedColumnName = "id", nullable = true)
	private T parent;

	@JsonIgnore
	@OneToMany(mappedBy = "parent", /* cascade=CascadeType.ALL, */ fetch=FetchType.LAZY)
	private List<T> children = new ArrayList<T>(0);

	public AuditableResource() {
		super();
	}
	public AuditableResource(String name) {
		this.setName(name);
	}
	public AuditableResource(String name, T parent) {
		this(name);
		this.setParent(parent);
	}
	
	@JsonIgnore
	@Transient
	public String getPathSeparator(){
		return PATH_SEPARATOR;
	}
	
	@PreUpdate
    @PrePersist
    public void normalizePath() throws UnsupportedEncodingException{
		// set path
		if(this.getPath() == null){
			StringBuffer path = new StringBuffer();
			if(this.getParent() != null){
				path.append(this.getParent().getPath());
			}
			path.append(getPathSeparator());
			path.append(this.getName());
			this.setPath(path.toString());
		}
		// set path level
		Integer pathLevel = StringUtils.countMatches(this.getPath(), getPathSeparator());
		this.setPathLevel(pathLevel.shortValue());
		
	}
	
	@Override
	public boolean equals(Object obj) {
		if (null == obj) {
			return false;
		}

		if (this == obj) {
			return true;
		}
		if (!(obj instanceof User)) {
			return false;
		}
		AuditableResource other = (AuditableResource) obj;
		EqualsBuilder builder = new EqualsBuilder();
		builder.appendSuper(super.equals(obj));
		builder.append(this.getName(), other.getName());
		builder.append(this.getPath(), other.getPath());
		return builder.isEquals();
	}
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public Short getPathLevel() {
		return pathLevel;
	}
	public void setPathLevel(Short pathLevel) {
		this.pathLevel = pathLevel;
	}
	public T getSameAs() {
		return sameAs;
	}
	public void setSameAs(T sameAs) {
		this.sameAs = sameAs;
	}
	public T getParent() {
		return parent;
	}
	public void setParent(T parent) {
		this.parent = parent;
	}
	public List<T> getChildren() {
		return children;
	}
	public void setChildren(List<T> children) {
		this.children = children;
	}

}
