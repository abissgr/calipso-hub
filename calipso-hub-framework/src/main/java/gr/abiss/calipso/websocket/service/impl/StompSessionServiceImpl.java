package gr.abiss.calipso.websocket.service.impl;


import java.io.Serializable;

import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

import gr.abiss.calipso.friends.repository.FriendshipRepository;
import gr.abiss.calipso.model.User;
import gr.abiss.calipso.model.dto.UserDTO;
import gr.abiss.calipso.tiers.service.AbstractModelServiceImpl;
import gr.abiss.calipso.userDetails.model.ICalipsoUserDetails;
import gr.abiss.calipso.websocket.Destinations;
import gr.abiss.calipso.websocket.message.IMessageResource;
import gr.abiss.calipso.websocket.message.StateUpdateMessage;
import gr.abiss.calipso.websocket.model.StompSession;
import gr.abiss.calipso.websocket.repository.StompSessionRepository;
import gr.abiss.calipso.websocket.service.StompSessionService;


@Named(StompSessionService.BEAN_ID)
@Transactional(readOnly = true)
public class StompSessionServiceImpl extends AbstractModelServiceImpl<StompSession, String, StompSessionRepository> implements StompSessionService {


	private static final Logger LOGGER = LoggerFactory.getLogger(StompSessionServiceImpl.class);

	private FriendshipRepository friendshipRepository;
	
	@Autowired
	public void setFriendshipRepository(FriendshipRepository friendshipRepository) {
		this.friendshipRepository = friendshipRepository;
	}


	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = false)
	@PreAuthorize(StompSession.PRE_AUTHORIZE_CREATE)
	public StompSession create(StompSession resource) {
		validateUser(resource);
		
		// get user id
		String userId = resource.getUser().getId();
		// count active stom sessions for user
		long count = this.repository.countForUser(userId).longValue();
		// create the stomp session
		resource = super.create(resource);
		
		// update user status and notify accordignly
		if(count == 0){
			long stompSessionCount = count + 1;
			
			sendStomSessionStatusUpdateToFriends(userId, stompSessionCount);
		}
		return resource;
	}


	public void sendStomSessionStatusUpdateToFriends(String userId, long stompSessionCount) {
		LOGGER.debug("SENDING {} {}", userId, stompSessionCount);
		Iterable<String> useernames = this.friendshipRepository.findAllFriendUsernames(userId);
		// create message
		StateUpdateMessage msg = new StateUpdateMessage();
		msg.setId(userId);
		msg.setResourceClass(UserDTO.class);
		msg.addModification("stompSessionCount", stompSessionCount);
		for(String username : useernames){
			LOGGER.debug("SENDING TO USER {}, msg: {}", username, msg);
			this.messagingTemplate.convertAndSendToUser(username, Destinations.USERQUEUE_UPDATES_STATE, msg);
		}
	}


	@Override
	@Transactional(readOnly = false)
	@PreAuthorize(StompSession.PRE_AUTHORIZE_UPDATE)
	public StompSession update(StompSession resource) {
		validateUser(resource);
		return super.update(resource);
	}
	
    /**
     * {@inheritDoc}
     */
	@Override
	@Transactional(readOnly = false)
	@PreAuthorize(StompSession.PRE_AUTHORIZE_DELETE)
	public void delete(@P("resource") StompSession sess) {
		sess = this.repository.getOne(sess.getId());
		long count = this.repository.countForUser(sess.getUser().getId()).longValue();
		super.delete(sess);
		// notify friends if user has gone offline
		if(count <= 1){
			sendStomSessionStatusUpdateToFriends(sess.getUser().getId(), 0);
		}
	}

    /**
     * {@inheritDoc}
     */
	@Override
	@Transactional(readOnly = false)
	@PreAuthorize(StompSession.PRE_AUTHORIZE_DELETE)
	public void delete(String id) {
		StompSession sess = this.repository.findOne(id);
		if(sess != null){
			long count = this.repository.countForUser(sess.getUser().getId()).longValue();
			super.delete(sess);
			// notify friends if user has gone offline
			if(count <= 1){
				sendStomSessionStatusUpdateToFriends(sess.getUser().getId(), 0);
			}
			
		}
	}


	public void validateUser(StompSession resource) {
		ICalipsoUserDetails ud = this.getPrincipal();
		LOGGER.info("userDetails: {}", ud);
		if(resource.getUser() == null){
			resource.setUser(new User(ud.getId()));
		}
		else if(!ud.getId().equals(resource.getUser().getId())){
			throw new IllegalArgumentException("Session user does not match current principal");
		}
	}
	
}

	

