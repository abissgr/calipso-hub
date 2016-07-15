package gr.abiss.calipso.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import gr.abiss.calipso.model.Friendship;
import gr.abiss.calipso.model.User;
import gr.abiss.calipso.tiers.repository.ModelRepository;import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for the Friendship entity.
 */
@SuppressWarnings("unused")
public interface FriendshipRepository extends ModelRepository<Friendship,String> {

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
    		+ "and status <> gr.abiss.calipso.model.types.FriendshipStatus.INVERSE")
    List<Friendship> findSentByCurrentUser();

    @Query("select f from Friendship f where f.requestRecipient.id = ?#{principal.id} "
    		+ "and status <> gr.abiss.calipso.model.types.FriendshipStatus.INVERSE")
    List<Friendship> findReceivedByCurrentUser();


    @Query("select case when count(f) > 0 then true else false end from Friendship f where "
    		+ "f.requestSender = ?1 and f.requestRecipient = ?2 "
    		+ " and (status <> gr.abiss.calipso.model.types.FriendshipStatus.ACCEPTED "
    		+ "		or status <> gr.abiss.calipso.model.types.FriendshipStatus.INVERSE)")
    Boolean existsEstablished(User one, User other);
    
    @Query("select case when count(f) > 0 then true else false end from Friendship f where "
    		+ " (f.requestSender = ?1 and f.requestRecipient = ?2) "
    		+ "	or (f.requestSender = ?2 and f.requestRecipient = ?1) ")
    Boolean existsAny(User one, User other);
}
