package gr.abiss.calipso.friends.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Formula;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Persistable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import gr.abiss.calipso.model.User;
import gr.abiss.calipso.model.interfaces.CalipsoPersistable;
import gr.abiss.calipso.tiers.annotation.ModelResource;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * A model representing a directional connection between two users. An
 * additional record with status INVERSE exists for each record with status
 * ACCEPTED. TODO: Refactor to Embeddable component
 * 
 */
@Entity
@IdClass(FriendshipId.class)
@Table(name = "friendship")
@ModelResource(path = Friendship.API_PATH, apiName = "Friendships", apiDescription = "Operations about friendships")
@ApiModel(value = "Friendship", description = "A model representing a directional connection between two users. ")
public class Friendship implements CalipsoPersistable<FriendshipId> {

	private static final Logger LOGGER = LoggerFactory.getLogger(Friendship.class);
	private static final long serialVersionUID = 1L;

	public static final String API_PATH = "friendships";

	@Id
	@ApiModelProperty(required = true, example = "{id: '[id]'}")
	@NotNull
	@ManyToOne(optional = false)
	@JoinColumn(name = "request_sender", nullable = false, updatable = false)
	private User requestSender;

	@Id
	@ApiModelProperty(required = true, example = "{id: '[id]'}")
	@NotNull
	@ManyToOne(optional = false)
	@JoinColumn(name = "request_recipient", nullable = false, updatable = false)
	private User requestRecipient;

	@ApiModelProperty(required = true, example = "{id: \"ACCEPTED\"}")
	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private FriendshipStatus status = FriendshipStatus.PENDING;

	public Friendship() {
	}



	public Friendship(User requestSender, User requestRecipient) {
		this.requestSender = requestSender;
		this.requestRecipient = requestRecipient;
	}



	@Override
	public FriendshipId getId() {
		return new FriendshipId(this.getRequestSender().getId(), this.getRequestRecipient().getId());
	}


	@Override
	public void setId(FriendshipId id) {
    	LOGGER.debug("setId: {}", id.toStringRepresentation());
	}

	@Override
	public boolean isNew() {
		return this.getRequestSender() == null;
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

}