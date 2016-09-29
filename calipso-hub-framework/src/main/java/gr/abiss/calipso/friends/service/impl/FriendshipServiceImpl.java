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
import gr.abiss.calipso.friends.model.FriendshipStatus;
import gr.abiss.calipso.friends.repository.FriendshipRepository;
import gr.abiss.calipso.friends.service.FriendshipService;
import gr.abiss.calipso.model.User;
import gr.abiss.calipso.model.dto.FriendshipDTO;
import gr.abiss.calipso.model.dto.UserDTO;
import gr.abiss.calipso.tiers.service.AbstractModelServiceImpl;
import gr.abiss.calipso.userDetails.model.ICalipsoUserDetails;
import gr.abiss.calipso.web.spring.ParameterMapBackedPageRequest;
import gr.abiss.calipso.websocket.Destinations;


@Named(FriendshipService.BEAN_ID)
@Transactional(readOnly = true)
public class FriendshipServiceImpl extends AbstractModelServiceImpl<Friendship, String, FriendshipRepository> implements FriendshipService {


	private static final Logger LOGGER = LoggerFactory.getLogger(FriendshipServiceImpl.class);
	
	/**
	 * Create a friendship request
	 */
	@Override
	@Transactional(readOnly = false)
	@PreAuthorize("hasRole('ROLE_USER')")
	public Friendship create(Friendship friendship) {
		// get current principal
		ICalipsoUserDetails userDetails = this.getPrincipal();

		// make sure the right sender is set if not empty
		if(friendship.getRequestSender() != null){
			// ensure sender is the current user
			if(!userDetails.getId().equals(friendship.getRequestSender().getId())){
				throw new IllegalArgumentException("Invalid friendship sender");
			}
		}
		else{
			// otherwise set as the current user
			friendship.setRequestSender(new User.Builder().id(userDetails.getId()).username(userDetails.getUsername()).build());
		}
		
		// check status if any
		if(friendship.getStatus() != null){
			if(!friendship.getStatus().equals(FriendshipStatus.PENDING)){
				throw new IllegalArgumentException("Invalid friendship status");
			}
		}
		// set automatically otherwise
		else{
			friendship.setStatus(FriendshipStatus.PENDING);
		}
	
		
		// make sure the friendship does not already exist
		if(this.repository.existsAny(friendship.getRequestSender(), friendship.getRequestRecipient())){
			throw new IllegalArgumentException("Friendship already exists");
		}
		
		// create
		friendship = super.create(friendship);
		if(friendship.getStatus().equals(FriendshipStatus.PENDING)){
			// notify request recepient
			String username = this.userRepository.findCompactUserById(friendship.getRequestRecipient().getId()).getUsername();
			LOGGER.debug("Sending friendship DTO to " + username);
			this.messagingTemplate.convertAndSendToUser(username, Destinations.USERQUEUE_FRIENDSHIPS, new FriendshipDTO(friendship));
		}

		// create inverse if accepted
		this.createInverseIfAccepted(friendship);
		
		return friendship;
	}
	
	/**
	 * Approve or reject a friendship request
	 */
	@Override
	@Transactional(readOnly = false)
	@PreAuthorize("hasRole('ROLE_USER')")
	public Friendship update(Friendship resource) {
		// Get current principal,
		ICalipsoUserDetails userDetails = this.getPrincipal();
		// new status,
		FriendshipStatus newStatus = resource.getStatus();
		// and persisted friendship entry to update.
		Friendship persistedFriendship = this.findById(resource.getId());

		// If not admin then
		if(!userDetails.isAdmin()){
			// validate recipient in persisted entry
			if(!userDetails.getId().equals(persistedFriendship.getRequestRecipient().getId())){ 
				throw new IllegalArgumentException("No entry found for recipient");
			}
		}
		
		// Only accepted/rejected allowed as new status value
		if(!newStatus.equals(FriendshipStatus.ACCEPTED) && !newStatus.equals(FriendshipStatus.REJECTED)){
			throw new IllegalArgumentException("Invalid friendship status");
		}
		
		// Update with new status
		persistedFriendship.setStatus(newStatus);
		persistedFriendship = super.update(persistedFriendship);
		
		// create inverse if accepted
		this.createInverseIfAccepted(persistedFriendship);
		
		return persistedFriendship;
	}

	/**
	 * Delete the friendship and it's inverse
	 */
	@Override
	@Transactional(readOnly = false)
	@PreAuthorize("hasRole('ROLE_USER')")
	public void delete(Friendship resource) {
		// get current principal
		ICalipsoUserDetails userDetails = this.getPrincipal();
		// if not admin
		if(!userDetails.isAdmin()){
			Friendship friendship = this.repository.findOne(resource.getId());
			// make sure a participant deletes 
			if(!userDetails.getId().equals(friendship.getRequestSender().getId()) 
					&& !userDetails.getId().equals(friendship.getRequestRecipient().getId()) ){
				throw new IllegalArgumentException("No entry found");
			}
		}
		// delete friendship and it's inverse
		this.repository.delete(resource.getRequestSender().getId(), resource.getRequestRecipient().getId());
	}

	private Friendship createInverseIfAccepted(Friendship friendship) {
		Friendship inverse = null;
		if(friendship.getStatus().equals(FriendshipStatus.ACCEPTED)){
			inverse = new Friendship(friendship.getRequestRecipient(), friendship.getRequestSender());
			inverse.setStatus(FriendshipStatus.INVERSE);
			super.create(inverse);
			// notify request sender

			String username = this.userRepository.findCompactUserById(friendship.getRequestSender().getId()).getUsername();
			LOGGER.debug("Sending friendship DTO to " + username);
			this.messagingTemplate.convertAndSendToUser(username, Destinations.USERQUEUE_FRIENDSHIPS, new FriendshipDTO(friendship));
		}
		return inverse;
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

	

}