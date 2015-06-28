package gr.abiss.calipso.model.base;

import gr.abiss.calipso.model.interfaces.ReportDataSetSubject;

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
public abstract class AbstractCategory<T extends AbstractCategory<T>> extends AuditableResource<T> implements ReportDataSetSubject{

	private static final long serialVersionUID = -1329254539598110186L;

	public AbstractCategory() {
		super();
	}
	public AbstractCategory(String name) {
		super(name);
	}

	public AbstractCategory(String name, T parent) {
		super(name, parent);
	}

	/**
	 * {@inheritDoc}}
	 * @see gr.abiss.calipso.model.interfaces.ReportDataSetSubject#getLabel()
	 */
	@Override
	public String getLabel(){
		return this.getName();
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

	
}
