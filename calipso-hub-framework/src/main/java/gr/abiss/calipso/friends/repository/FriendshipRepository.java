package gr.abiss.calipso.friends.repository;


import java.util.List;
import java.util.Optional;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import gr.abiss.calipso.friends.model.Friendship;
import gr.abiss.calipso.model.User;
import gr.abiss.calipso.model.dto.UserDTO;
import gr.abiss.calipso.repository.UserRepository;
import gr.abiss.calipso.tiers.repository.ModelRepository;

/**
 * Spring Data JPA repository for the Friendship entity.
 */
@SuppressWarnings("unused")
@JaversSpringDataAuditable
public interface FriendshipRepository extends ModelRepository<Friendship,String> {

	public static final String SELECT_USERDTO = "select new gr.abiss.calipso.model.dto.UserDTO(friendship.requestRecipient.id, "
			+ "		friendship.requestRecipient.firstName, "
			+ "		friendship.requestRecipient.lastName, "
			+ "		friendship.requestRecipient.username, "
			+ "		friendship.requestRecipient.email, "
			+ "		friendship.requestRecipient.emailHash,"
			+ "		friendship.requestRecipient.avatarUrl,"
			+ "		friendship.requestRecipient.bannerUrl,"
			+ "		friendship.requestRecipient.stompSessionCount"
			+ ") ";
	
	static final String FROM__FRIENDS_BY_USERID = " from Friendship friendship where friendship.requestSender.id =  ?1 "
			+ "and (friendship.status = gr.abiss.calipso.friends.model.FriendshipStatus.ACCEPTED or friendship.status = gr.abiss.calipso.friends.model.FriendshipStatus.INVERSE)";
	
	static final String QUERY_FRIEND_USERNAMES_BY_USERID =  "select friendship.requestRecipient.username " + FROM__FRIENDS_BY_USERID;
	
	static final String QUERY_FRIENDS_BY_USERID = SELECT_USERDTO + FROM__FRIENDS_BY_USERID;
	
	/**
	 * Native modifying query to delete along with inverse
	 * @param oneUserId
	 * @param otherUserId
	 */
    @Modifying
    @Query(value="DELETE FROM friendship WHERE (request_sender = ?1 and request_recipient = ?2) "
    		+ " or (request_sender = ?2 and request_recipient = ?1)", nativeQuery=true)
    void delete(String oneUserId, String otherUserId);
    
    @Query("select f from Friendship f where f.requestSender.id = ?#{principal.id} "
    		+ "and status <> gr.abiss.calipso.friends.model.FriendshipStatus.INVERSE")
    List<Friendship> findSentByCurrentUser();

    @Query("select f from Friendship f where f.requestRecipient.id = ?#{principal.id} "
    		+ "and status <> gr.abiss.calipso.friends.model.FriendshipStatus.INVERSE")
    List<Friendship> findReceivedByCurrentUser();


    @Query("select case when count(f) > 0 then true else false end from Friendship f where "
    		+ "f.requestSender = ?1 and f.requestRecipient = ?2 "
    		+ " and (status <> gr.abiss.calipso.friends.model.FriendshipStatus.ACCEPTED "
    		+ "		or status <> gr.abiss.calipso.friends.model.FriendshipStatus.INVERSE)")
    Boolean existsEstablished(User one, User other);
    
    @Query("select case when count(f) > 0 then true else false end from Friendship f where "
    		+ " (f.requestSender = ?1 and f.requestRecipient = ?2) "
    		+ "	or (f.requestSender = ?2 and f.requestRecipient = ?1) ")
    Boolean existsAny(User one, User other);


	@Query(QUERY_FRIEND_USERNAMES_BY_USERID)
	public Iterable<String> findAllFriendUsernames(String userId);

	@Query(QUERY_FRIENDS_BY_USERID)
	public Iterable<UserDTO> findAllFriends(String userId);

	@Query(QUERY_FRIENDS_BY_USERID)
	public Page<UserDTO> findAllFriendsPaginated(String userId, Pageable pageRequest);
}
