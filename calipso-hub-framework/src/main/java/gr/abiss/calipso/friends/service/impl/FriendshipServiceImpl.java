package gr.abiss.calipso.friends.service.impl;

import java.util.Map;

import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

import gr.abiss.calipso.friends.model.Friendship;
import gr.abiss.calipso.friends.model.FriendshipId;
import gr.abiss.calipso.friends.model.FriendshipStatus;
import gr.abiss.calipso.friends.repository.FriendshipRepository;
import gr.abiss.calipso.friends.service.FriendshipService;
import gr.abiss.calipso.model.User;
import gr.abiss.calipso.model.dto.FriendshipDTO;
import gr.abiss.calipso.model.dto.UserDTO;
import gr.abiss.calipso.tiers.service.AbstractModelServiceImpl;
import gr.abiss.calipso.userDetails.model.ICalipsoUserDetails;
import gr.abiss.calipso.web.spring.ParameterMapBackedPageRequest;
import gr.abiss.calipso.web.spring.UniqueConstraintViolationException;
import gr.abiss.calipso.websocket.Destinations;
import gr.abiss.calipso.websocket.message.ActivityNotificationMessage;


@Named(FriendshipService.BEAN_ID)
@Transactional(readOnly = true)
public class FriendshipServiceImpl extends AbstractModelServiceImpl<Friendship, FriendshipId, FriendshipRepository> implements FriendshipService {


	private static final Logger LOGGER = LoggerFactory.getLogger(FriendshipServiceImpl.class);


	/**
	 * Create a friendship request
	 */
	@Override
	@Transactional(readOnly = false)
	@PreAuthorize("hasRole('ROLE_USER')")
	public Friendship create(Friendship resource) {
		LOGGER.debug("create: {}", resource);
		return this.saveRelationship(resource);
	}

	protected void validateSender(Friendship resource) {
		
		// get current principal
		String userDetailsId = this.getPrincipal().getId();

		// set current user as  sender if the latter is empty
		if(resource.getId().getOwner() == null){
			resource.getId().setOwner(new User(userDetailsId));
		}
		
		// verify principal == owner
		if( !userDetailsId.equals(resource.getId().getOwner().getId())){
			throw new UniqueConstraintViolationException("Invalid friendship owner.");
		}
		
		// verify principal != friend
		if(userDetailsId.equals(resource.getId().getFriend().getId())){
			throw new UniqueConstraintViolationException("Befriending yourself is not allowed.");
		}
		
		LOGGER.debug("validateSender returns: {}", resource);
	}
	
	protected Friendship saveRelationship(Friendship resource) {


		validateSender(resource);

		// get the persisted record, if any, null otherwise
		FriendshipStatus currentStatus = this.repository.getCurrentStatus(resource.getId());
		
		// validate next status
		boolean allowedNext = FriendshipStatus.isAllowedNext(currentStatus, resource.getStatus());
		LOGGER.debug("saveRelationship, allowedNext: {}", allowedNext);
		if(!allowedNext){
			throw new IllegalArgumentException("Cannot save with given status: " + resource.getStatus());
		}

		
		
		resource = saveSingle(resource);
		

		
		// update inverse if needed
		FriendshipStatus inverseStatus = FriendshipStatus.getApplicableInverse(resource.getStatus());
		if(inverseStatus != null){
			Friendship inverse = new Friendship(resource.getInverseId(), inverseStatus);
			saveSingle(inverse);
		}
		
				
		return resource;
	}

	protected Friendship saveSingle(Friendship resource) {
		LOGGER.debug("saveSingle: {}", resource);
		// if delete
		if(FriendshipStatus.DELETE.equals(resource.getStatus())){
			this.repository.delete(resource);
		}
		else{
			// persist changes
			resource = this.repository.save(resource);
			
		}
		
		// notify this side's owner of appropriae statuses
		if(resource.getStatus().equals(FriendshipStatus.PENDING) || resource.getStatus().equals(FriendshipStatus.CONFIRMED)){
			// notify this side of pending request
			String username = this.userRepository.findCompactUserById(resource.getId().getOwner().getId()).getUsername();
			LOGGER.debug("Sending friendship DTO to " + username);
			this.messagingTemplate.convertAndSendToUser(username, Destinations.USERQUEUE_FRIENDSHIPS, new FriendshipDTO(resource));
		}
		return resource;
	}
	
	/**
	 * Approve or reject a friendship request
	 */
	@Override
	@Transactional(readOnly = false)
	@PreAuthorize("hasRole('ROLE_USER')")
	public Friendship update(Friendship resource) {
		LOGGER.debug("update: {}", resource);
		return this.saveRelationship(resource);
	}
	


	/**
	 * Delete the friendship and it's inverse
	 */
	@Override
	@Transactional(readOnly = false)
	@PreAuthorize("hasRole('ROLE_USER')")
	public void delete(Friendship resource) {
		resource.setStatus(FriendshipStatus.DELETE);
		this.saveRelationship(resource);
	}
	
	
	@Override
	@PreAuthorize("hasRole('ROLE_USER')")
	public Page<Friendship> findAll(Pageable pageRequest) {
		// get current principal
		ICalipsoUserDetails userDetails = this.getPrincipal();
		// if not admin
		if(userDetails != null && !userDetails.isAdmin()){
			// make sure only a users inbox or outbox are returned
			Map<String, String[]> params = ((ParameterMapBackedPageRequest) pageRequest).getParameterMap();
			boolean hasPermission = false;
			String[] attrsToCheck = {"requestSender", "requestRecipient"};
			String validUserId = userDetails.getId();
			// by checking sender 
			for(String attrToCheck : attrsToCheck){
				String[] values = params.get(attrToCheck);
				if(values != null){
					// check if valid
					if(values.length == 1 && validUserId.equals(values[0])){
						hasPermission = true;
					}
					// remove otherwise
					else{
						params.remove(attrToCheck);
					}
				}
			}
			
			if(!hasPermission){
				throw new IllegalArgumentException("No entries found"); 
			}
			
		}
		return super.findAll(pageRequest);
	}
	

	@Override
	@PreAuthorize("hasRole('ROLE_USER')")
	public Iterable<UserDTO> findAllMyFriends() {
        return repository.findAllFriends(this.getPrincipal().getId());
	}

	@Override
	@PreAuthorize("hasRole('ROLE_USER')")
	public Page<UserDTO> findAllMyFriendsPaginated(Pageable pageRequest) {
        return repository.findAllFriendsPaginated(this.getPrincipal().getId(), pageRequest);
	}


	@Override
	@PreAuthorize("hasRole('ROLE_USER')")
	public void sendStompActivityMessageToOnlineFriends(ActivityNotificationMessage msg) {
		
		// get online friends
		Iterable<String> useernames = this.repository.findAllStompOnlineFriendUsernames(this.getPrincipal().getId());
		
		this.sendStompActivityMessage(msg, useernames);
	}


}