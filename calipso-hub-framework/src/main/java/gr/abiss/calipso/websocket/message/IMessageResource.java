package gr.abiss.calipso.websocket.message;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Generic interface for subject and object parts of a {@link IActivityNotificationMessage}
 * 
 * 
 * @param <ID> the ID type
 * 
 * @see MessageResource
 */
@JsonTypeInfo(
	    use = JsonTypeInfo.Id.MINIMAL_CLASS,
	    include = JsonTypeInfo.As.PROPERTY,
	    property = IMessageResource.CLASS_ATTRIBUTE_NAME)
public interface IMessageResource<ID extends Serializable> extends Serializable{
	
	public static final String CLASS_ATTRIBUTE_NAME = "@class";

	public ID getId();

	public void setId(ID id);

	public String getName();

	public void setName(String name);
}