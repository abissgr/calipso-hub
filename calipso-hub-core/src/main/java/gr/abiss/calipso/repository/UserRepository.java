package gr.abiss.calipso.repository;

import gr.abiss.calipso.jpasearch.repository.BaseRepository;
import gr.abiss.calipso.model.User;

import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends BaseRepository<User, String> {

	@Query("select u from User u where u.confirmationToken = ?1")
	public User findByConfirmationToken(String token);

	@Query("select u from User u where (UPPER(u.email) = UPPER(?1) or UPPER(u.userName) = UPPER(?1)) and u.userPassword = ?2 and u.active = true")
	public User findByCredentials(String userNameOrEmail, String password);

	// @Query("select u from User u LEFT JOIN FETCH u.roles where UPPER(u.email) = UPPER(?1) or UPPER(u.userName) = UPPER(?1)) ")
	@Query("select u from User u where UPPER(u.email) = UPPER(?1) or UPPER(u.userName) = UPPER(?1)) ")
	public User findByUserNameOrEmail(String userNameOrEmail);
}
