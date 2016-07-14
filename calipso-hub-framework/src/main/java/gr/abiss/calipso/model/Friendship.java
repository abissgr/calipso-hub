package gr.abiss.calipso.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import gr.abiss.calipso.model.User;
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
	@JoinColumn(name = "request_recepient", nullable=false)
	private User requestRecepient;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable=false)
	private FriendshipStatus status = FriendshipStatus.PENDING;

	public Friendship() {
	}

	public Friendship(User requestSender, User requestRecepient) {
		this.requestSender = requestSender;
		this.requestRecepient = requestRecepient;
	}

	public User getRequestSender() {
		return requestSender;
	}

	public void setRequestSender(User requestSender) {
		this.requestSender = requestSender;
	}

	public User getRequestRecepient() {
		return requestRecepient;
	}

	public void setRequestRecepient(User requestRecepient) {
		this.requestRecepient = requestRecepient;
	}

	public FriendshipStatus getStatus() {
		return status;
	}

	public void setStatus(FriendshipStatus status) {
		this.status = status;
	}

	public static class Builder {
		private User requestSender;
		private User requestRecepient;
		private FriendshipStatus status;

		public Builder requestSender(User requestSender) {
			this.requestSender = requestSender;
			return this;
		}

		public Builder requestRecepient(User requestRecepient) {
			this.requestRecepient = requestRecepient;
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
		this.requestSender = builder.requestSender;
		this.requestRecepient = builder.requestRecepient;
		this.status = builder.status;
	}
}