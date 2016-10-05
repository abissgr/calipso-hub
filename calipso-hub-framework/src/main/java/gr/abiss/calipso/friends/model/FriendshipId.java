package gr.abiss.calipso.friends.model;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import gr.abiss.calipso.friends.binding.FriendshipIdDeserializer;
import gr.abiss.calipso.friends.binding.FriendshipIdSerializer;
import gr.abiss.calipso.model.User;
import gr.abiss.calipso.web.spring.UniqueConstraintViolationException;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Composite primary key for {@Friendship}
 */

@Embeddable
@ApiModel(value = "FriendshipId", description = "An {@link javax.persistence;Embeddable} JPA composite key. "
		+ "The custom implementatin provides supplort to all relevant de)serialization components "
		+ "(JSON, request mappings, path/param variables etc.) " + "for both [ownerId" + FriendshipId.SPLIT_CHAR
		+ "friendId]" + " and [friendId] string representations.")
@JsonSerialize(using = FriendshipIdSerializer.class)
@JsonDeserialize(using = FriendshipIdDeserializer.class)
public class FriendshipId implements Serializable {

	static final String SPLIT_CHAR = "_";

	private static final long serialVersionUID = 943487589755250322L;

	private static final Logger LOGGER = LoggerFactory.getLogger(FriendshipId.class);

	public static final String EMPTY = "";

	@ApiModelProperty(required = true, example = "{id: '[id]'}")
	@NotNull
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "owner", nullable = false, updatable = false)
	private User owner;

	@ApiModelProperty(required = true, example = "{id: '[id]'}")
	@NotNull
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "inverse_user", nullable = false, updatable = false)
	private User friend;

	public FriendshipId() {
	}

	public FriendshipId(@NotNull String value) {
		this(value.split(FriendshipId.SPLIT_CHAR));
	}

	public FriendshipId(String... parts) {
		if (parts.length == 2) {
			this.owner = new User(parts[0]);
			this.friend = new User(parts[1]);
		} else if (parts.length == 1) {
			this.friend = new User(parts[0]);
		}

	}

	public FriendshipId(String owner, @NotNull String friend) {
		if (owner != null) {
			this.owner = new User(owner);
		}
		this.friend = new User(friend);
	}

	public FriendshipId(User owner, @NotNull User friend) {
		if (owner != null) {
			this.owner = owner;
		}
		this.friend = friend;
	}

	public String toStringRepresentation() {

		String sender = idOrNEmpty(this.getOwner());
		String recipient = idOrNEmpty(this.getFriend());

		StringBuffer s = new StringBuffer(sender);
		if (StringUtils.isNoneBlank(sender, recipient)) {
			s.append(SPLIT_CHAR);
		}
		s.append(recipient);

		String id = s.toString();

		return StringUtils.isNotBlank(id) ? id : null;

	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("owner", idOrNull(this.getOwner()))
				.append("friend", idOrNull(this.getFriend())).toString();
	}

	private String idOrNull(User user) {
		return user != null ? user.getId() : null;
	}

	private String idOrNEmpty(User user) {
		return user != null ? user.getId() : EMPTY;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(idOrNull(this.getOwner())).append(idOrNull(this.getFriend())).toHashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (FriendshipId.class.isAssignableFrom(obj.getClass())) {
			final FriendshipId other = (FriendshipId) obj;
			return new EqualsBuilder().append(idOrNull(this.getOwner()), idOrNull(other.getOwner()))
					.append(idOrNull(this.getFriend()), idOrNull(other.getFriend())).isEquals();
		} else {
			return false;
		}
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public User getFriend() {
		return friend;
	}

	public void setFriend(User friend) {
		this.friend = friend;
	}

}