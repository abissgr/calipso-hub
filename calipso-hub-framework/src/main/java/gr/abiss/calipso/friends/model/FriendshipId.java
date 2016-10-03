package gr.abiss.calipso.friends.model;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import gr.abiss.calipso.friends.binding.FriendshipIdDeserializer;
import gr.abiss.calipso.friends.binding.FriendshipIdSerializer;
import gr.abiss.calipso.web.spring.UniqueConstraintViolationException;

/**
 * Composite primary key for {@Friendship}
 */

@JsonSerialize(using = FriendshipIdSerializer.class)
@JsonDeserialize(using = FriendshipIdDeserializer.class)
public class FriendshipId implements Serializable {

	private static final Logger LOGGER = LoggerFactory.getLogger(FriendshipId.class);

	private static final long serialVersionUID = 1L;

	private String requestSender;

	private String requestRecipient;

	public FriendshipId() {
		super();
	}

	public FriendshipId(String value) {
		String[] values = StringUtils.isNotBlank(value) ? value.split("_") : new String[]{};
		
		if (values.length != 2) {
			throw new UniqueConstraintViolationException("Validation failed", "Invalid string representation of FriendshipId: " + value, true);
		}

		this.requestSender = values[0];
		this.requestRecipient = values[1];
	
	}

	public FriendshipId(String requestSender, String requestRecipient) {
		super();
		this.requestSender = requestSender;
		this.requestRecipient = requestRecipient;
	}

	public String toStringRepresentation() {
		String s = StringUtils.isNoneBlank(this.getRequestSender()) 
				? new StringBuffer(this.getRequestSender()).append("_").append(getRequestRecipient()).toString() 
				: null;
		return s;
		
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("requestSender", requestSender)
				.append("requestRecipient", requestRecipient).toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((requestRecipient == null) ? 0 : requestRecipient.hashCode());
		result = prime * result + ((requestSender == null) ? 0 : requestSender.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FriendshipId other = (FriendshipId) obj;
		if (getRequestRecipient() == null) {
			if (other.getRequestRecipient() != null)
				return false;
		} else if (!getRequestRecipient().equals(other.getRequestRecipient()))
			return false;
		if (getRequestSender() == null) {
			if (other.getRequestSender() != null)
				return false;
		} else if (!getRequestSender().equals(other.getRequestSender()))
			return false;
		return true;
	}

	public String getRequestSender() {
		return requestSender;
	}

	public void setRequestSender(String requestSender) {
		this.requestSender = requestSender;
	}

	public String getRequestRecipient() {
		return requestRecipient;
	}

	public void setRequestRecipient(String requestRecipient) {
		this.requestRecipient = requestRecipient;
	}

}