package gr.abiss.calipso.service.impl;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.transaction.annotation.Transactional;

import gr.abiss.calipso.model.Friendship;
import gr.abiss.calipso.model.User;
import gr.abiss.calipso.model.types.FriendshipStatus;
import gr.abiss.calipso.repository.FriendshipRepository;
import gr.abiss.calipso.repository.UserRepository;
import gr.abiss.calipso.service.FriendshipService;
import gr.abiss.calipso.tiers.service.AbstractModelServiceImpl;
import gr.abiss.calipso.userDetails.model.ICalipsoUserDetails;


@Named("friendshipService")
@Transactional(readOnly = true)
public class FriendshipServiceImpl extends AbstractModelServiceImpl<Friendship, String, FriendshipRepository> implements FriendshipService {

	protected UserRepository userRepository;

	@Inject
	public void setUserRepository(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
//	@Override
//	public Friendship findOneByFriends(User one, User other) {
//		return this.repository.findOneByFriends(one, other).orElse(null);
//	}

	/**
	 * Create a friendship request
	 */
	@Override
	@Transactional(readOnly = false)
	public Friendship create(Friendship friendship) {
		// get current principal
		ICalipsoUserDetails userDetails = this.getPrincipal();
		
		// if not admin, then
		if(!userDetails.isAdmin()){
			
			// make sure the right sender is set if not empty
			if(friendship.getRequestSender() != null){
				// if not owned sender
				if(!userDetails.getId().equals(friendship.getRequestSender())){
					throw new IllegalArgumentException("Invalid friendship sender");
				}
			}
			else{
				// otherwise set as the current user
				friendship.setRequestSender(new User(userDetails.getId()));
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
		}
		
		// make sure the friendship does not already exist
		if(this.repository.existsAny(friendship.getRequestSender(), friendship.getRequestRecipient())){
			throw new IllegalArgumentException("Friendship already exists");
		}
		
		// create
		friendship = super.create(friendship);
		// create inverse if accepted
		this.createInverseIfAccepted(friendship);
		
		return friendship;
	}
	
	/**
	 * Approve or reject a friendship request
	 */
	@Override
	@Transactional(readOnly = false)
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
			if(!userDetails.getId().equals(persistedFriendship.getRequestRecipient())){ 
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
	public void delete(Friendship resource) {
		this.repository.delete(resource.getRequestSender().getId(), resource.getRequestRecipient().getId());
	}

	private Friendship createInverseIfAccepted(Friendship friendship) {
		Friendship inverse = null;
		if(friendship.getStatus().equals(FriendshipStatus.ACCEPTED)){
			inverse = new Friendship(friendship.getRequestRecipient(), friendship.getRequestSender());
			inverse.setStatus(FriendshipStatus.INVERSE);
			super.create(inverse);
		}
		return inverse;
	}
	
	

}