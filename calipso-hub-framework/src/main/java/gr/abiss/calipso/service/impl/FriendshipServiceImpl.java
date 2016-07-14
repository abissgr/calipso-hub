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
		
		// if not admin, make sure principal is making the request and status is "invited"
		if(userDetails == null || !userDetails.isAdmin()){
			if(!userDetails.getId().equals(friendship.getRequestSender())){
				throw new IllegalArgumentException("Invalid friendship sender");
			}
			if(!friendship.getStatus().equals(FriendshipStatus.PENDING)){
				throw new IllegalArgumentException("Invalid friendship status");
			}
		}
		
		// make sure the friendship does not already exist
		if(this.repository.existsAny(friendship.getRequestSender(), friendship.getRequestRecepient())){
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
	public Friendship update(Friendship friendship) {
		// get current principal
		ICalipsoUserDetails userDetails = this.getPrincipal();
		
		// if not admin, make sure principal is making the request and status is "invited"
		if(!userDetails.isAdmin()){
			if(!userDetails.getId().equals(friendship.getRequestRecepient())){
				throw new IllegalArgumentException("Invalid friendship recepient");
			}
		}
		// only accepted/rejected statuses allowed
		FriendshipStatus status = friendship.getStatus();
		if(!status.equals(FriendshipStatus.ACCEPTED) && !status.equals(FriendshipStatus.REJECTED)){
			throw new IllegalArgumentException("Invalid friendship status");
		}
		
		// create
		friendship = super.update(friendship);
		// create inverse if accepted
		this.createInverseIfAccepted(friendship);
		
		return friendship;
	}

	/**
	 * Delete the friendship and it's inverse
	 */
	@Override
	@Transactional(readOnly = false)
	public void delete(Friendship resource) {
		this.repository.delete(resource.getRequestSender().getId(), resource.getRequestRecepient().getId());
	}

	private Friendship createInverseIfAccepted(Friendship friendship) {
		Friendship inverse = null;
		if(friendship.getStatus().equals(FriendshipStatus.ACCEPTED)){
			inverse = new Friendship(friendship.getRequestRecepient(), friendship.getRequestSender());
			inverse.setStatus(FriendshipStatus.INVERSE);
			super.create(inverse);
		}
		return inverse;
	}
	
	

}