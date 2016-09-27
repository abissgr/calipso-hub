package gr.abiss.calipso.websocket.message;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Base class for subject and object parts of a {@link IActivityNotificationMessage}
 */
public class MessageResource<ID extends Serializable> implements IMessageResource<ID> {

	/**
	 * The resource ID
	 */
	public ID id;
	
	/**
	 * The resource human-readable name
	 */
	public String name;

	public MessageResource() {
		super();
	}

	public MessageResource(ID id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	public ID getId() {
		return id;
	}

	public void setId(ID id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
