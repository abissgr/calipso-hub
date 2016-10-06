package gr.abiss.calipso.friends.repository;


import java.util.List;
import java.util.Optional;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import gr.abiss.calipso.friends.model.Friendship;
import gr.abiss.calipso.friends.model.FriendshipId;
import gr.abiss.calipso.friends.model.FriendshipStatus;
import gr.abiss.calipso.model.User;
import gr.abiss.calipso.model.dto.UserDTO;
import gr.abiss.calipso.repository.UserRepository;
import gr.abiss.calipso.tiers.repository.ModelRepository;
import gr.abiss.calipso.websocket.model.StompSession;

/**
 * Spring Data JPA repository for the Friendship entity.
 */
@SuppressWarnings("unused")
//@JaversSpringDataAuditable
public interface FriendshipRepository extends ModelRepository<Friendship,FriendshipId> {

	public static final String SELECT_FRIEND_AS_USERDTO = "select new gr.abiss.calipso.model.dto.UserDTO(friendship.id.friend.id, "
			+ "		friendship.id.friend.firstName, "
			+ "		friendship.id.friend.lastName, "
			+ "		friendship.id.friend.username, "
			+ "		friendship.id.friend.email, "
			+ "		friendship.id.friend.emailHash,"
			+ "		friendship.id.friend.avatarUrl,"
			+ "		friendship.id.friend.bannerUrl,"
			+ "		friendship.id.friend.stompSessionCount"
			+ ") ";


	public static final String IS_FRIEND = " (friendship.id.owner.id =  ?1 "
			+ "and friendship.status = gr.abiss.calipso.friends.model.FriendshipStatus.CONFIRMED) ";
	

	static final String FROM__FRIENDS_BY_USERID = " from Friendship friendship where " + IS_FRIEND;
	// 
	static final String FROM__STOMPONLINE_FRIENDS_BY_USERID = " from Friendship friendship where " + IS_FRIEND + " and friendship.id.friend.stompSessionCount > 0 ";

	static final String QUERY_FRIEND_USERNAMES_BY_USERID =  "select friendship.id.friend.username " + FROM__FRIENDS_BY_USERID;
	static final String QUERY_STOMPONLINE_FRIEND_USERNAMES_BY_USERID =  "select friendship.id.friend.username " + FROM__STOMPONLINE_FRIENDS_BY_USERID;
	
	static final String QUERY_FRIENDS_BY_USERID = SELECT_FRIEND_AS_USERDTO + FROM__FRIENDS_BY_USERID;
	
	@Query(QUERY_STOMPONLINE_FRIEND_USERNAMES_BY_USERID)
	public List<String> findAllStompOnlineFriendUsernames(String userId);
    
//    @Query("select f from Friendship f where f.id.owner.id = ?#{principal.id} "
//    		+ "and ( status <> gr.abiss.calipso.friends.model.FriendshipStatus.CONFIRMED_INVERSE or  status <> gr.abiss.calipso.friends.model.FriendshipStatus.BLOCK_INVERSE) ")
//    List<Friendship> findSentByCurrentUser();
//
//    @Query("select f from Friendship f where f.id.friend.id = ?#{principal.id} "
//    		+ "and ( status <> gr.abiss.calipso.friends.model.FriendshipStatus.CONFIRMED_INVERSE or  status <> gr.abiss.calipso.friends.model.FriendshipStatus.BLOCK_INVERSE) ")
//    List<Friendship> findReceivedByCurrentUser();


//    @Query("select case when count(f) > 0 then true else false end from Friendship f where "
//    		+ "f.id.owner = ?1 and f.id.friend = ?2 "
//    		+ " and status = gr.abiss.calipso.friends.model.FriendshipStatus.CONFIRMED ")
//    Boolean existsConfirmed(User one, User other);
//    
//    @Query("select case when count(f) > 0 then true else false end from Friendship f where "
//    		+ " (f.user = ?1 and f.friend = ?2) ")
//    Boolean existsAny(User one, User other);


	@Query("select f.status from Friendship f where f.id = ?1 ")
	public FriendshipStatus getCurrentStatus(FriendshipId id);
	
	@Query(QUERY_FRIEND_USERNAMES_BY_USERID)
	public Iterable<String> findAllFriendUsernames(String userId);


	@Query(QUERY_FRIENDS_BY_USERID)
	public Iterable<UserDTO> findAllFriends(String userId);

	@Query(QUERY_FRIENDS_BY_USERID)
	public Page<UserDTO> findAllFriendsPaginated(String userId, Pageable pageRequest);
}
