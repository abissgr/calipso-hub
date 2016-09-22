package gr.abiss.calipso.websocket.message;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Generic interface for subject and object parts of a {@link NotificationMessage}
 * @param <ID> the ID type
 * 
 * @see AbstractMessageResource
 */
@JsonTypeInfo(
	    use = JsonTypeInfo.Id.MINIMAL_CLASS,
	    include = JsonTypeInfo.As.PROPERTY,
	    property = "@class")
public interface MessageResource<ID extends Serializable> {

	public ID getId();

	public void setId(ID id);

	public String getName();

	public void setName(String name);
}