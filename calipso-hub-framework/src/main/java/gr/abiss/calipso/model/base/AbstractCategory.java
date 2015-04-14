package gr.abiss.calipso.model.base;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;


import org.apache.commons.lang3.builder.EqualsBuilder;

import com.fasterxml.jackson.annotation.JsonIgnore;


/**
 * Resource categories are hierarchical, have aliases and can be used as tags
 */
@MappedSuperclass
public abstract class AbstractCategory<T extends AbstractCategory<T>> extends AuditableResource {

	private static final long serialVersionUID = -1329254539598110186L;

	@JsonIgnore
	@ManyToOne(/* cascade=CascadeType.ALL, */fetch = FetchType.EAGER)
	@JoinColumn(name = "same_as", referencedColumnName = "id", nullable = true)
	private T sameAs;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent", referencedColumnName = "id", nullable = true)
	private T parent;

	@JsonIgnore
	@OneToMany(mappedBy = "parent", /* cascade=CascadeType.ALL, */ fetch=FetchType.LAZY)
	private List<T> children = new ArrayList<T>(0);
	
	public AbstractCategory(String name) {
		this.setName(name);
	}
	
	public AbstractCategory(String name, T parent) {
		this(name);
		this.setParent(parent);
	}
	@PreUpdate
    @PrePersist
    public void normalizePath() throws UnsupportedEncodingException{
		if(this.getPath() == null){
			StringBuffer path = new StringBuffer();
			if(this.getParent() != null){
				path.append(this.getParent().getPath());
			}
			path.append('/');
			path.append(URLEncoder.encode(this.getName(), "UTF-8"));
			this.setPath(path.toString());
		
		}
	}
	
	@Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof AbstractCategory<?>)) {
            return false;
        }
        AbstractCategory<?> that = (AbstractCategory<?>) obj;
        EqualsBuilder eb = new EqualsBuilder();
        eb.append(this.getName(), that.getName());
        eb.append(this.getPath(), that.getPath());
        return eb.isEquals();
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
