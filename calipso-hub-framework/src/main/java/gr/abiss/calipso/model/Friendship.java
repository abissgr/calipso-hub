package gr.abiss.calipso.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import gr.abiss.calipso.model.User;
import gr.abiss.calipso.model.base.PartiallyUpdateable;
import gr.abiss.calipso.model.entities.AbstractAuditable;
import gr.abiss.calipso.model.types.FriendshipStatus;
import gr.abiss.calipso.tiers.annotation.ModelResource;
import io.swagger.annotations.ApiModel;

/**
 * A model representing a directional connection between two users. 
 * An additional record with status INVERSE exists 
 * for each record with status ACCEPTED.
 * TODO: Refactor to Embeddable component
 * 
 */
@Entity
@Table(name = "friendship")
@ModelResource(path = "friendships", apiName = "Friendships", apiDescription = "Operations about friendships")
@ApiModel(value = "Friendship", description = "A model representing a directional connection between two users. ")
public class Friendship extends AbstractAuditable<User> {
	private static final long serialVersionUID = 1L;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "request_sender", nullable=false)
	private User requestSender;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "request_recipient", nullable=false)
	private User requestRecipient;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable=false)
	private FriendshipStatus status = FriendshipStatus.PENDING;

	public Friendship() {
	}

	public Friendship(User requestSender, User requestRecipient) {
		this.requestSender = requestSender;
		this.requestRecipient = requestRecipient;
	}

	public User getRequestSender() {
		return requestSender;
	}

	public void setRequestSender(User requestSender) {
		this.requestSender = requestSender;
	}

	public User getRequestRecipient() {
		return requestRecipient;
	}

	public void setRequestRecipient(User requestRecipient) {
		this.requestRecipient = requestRecipient;
	}

	public FriendshipStatus getStatus() {
		return status;
	}

	public void setStatus(FriendshipStatus status) {
		this.status = status;
	}

	public static class Builder {
		private String id;
		private User requestSender;
		private User requestRecipient;
		private FriendshipStatus status;

		public Builder id(String id) {
			this.id = id;
			return this;
		}
		
		public Builder requestSender(User requestSender) {
			this.requestSender = requestSender;
			return this;
		}

		public Builder requestRecipient(User requestRecipient) {
			this.requestRecipient = requestRecipient;
			return this;
		}

		public Builder status(FriendshipStatus status) {
			this.status = status;
			return this;
		}

		public Friendship build() {
			return new Friendship(this);
		}
	}

	private Friendship(Builder builder) {
		this.setId(builder.id);
		this.requestSender = builder.requestSender;
		this.requestRecipient = builder.requestRecipient;
		this.status = builder.status;
	}


}