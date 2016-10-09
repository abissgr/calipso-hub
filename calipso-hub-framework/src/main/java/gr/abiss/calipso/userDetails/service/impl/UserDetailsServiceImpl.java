/**
 * calipso-hub-framework - A full stack, high level framework for lazy application hackers.
 * Copyright © 2005 Manos Batsis (manosbatsis gmail)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package gr.abiss.calipso.userDetails.service.impl;

import gr.abiss.calipso.model.User;
import gr.abiss.calipso.service.UserService;
import gr.abiss.calipso.userDetails.integration.UserDetailsConfig;
import gr.abiss.calipso.userDetails.model.ICalipsoUserDetails;
import gr.abiss.calipso.userDetails.model.UserDetails;
import gr.abiss.calipso.userDetails.service.UserDetailsService;
import gr.abiss.calipso.userDetails.util.DuplicateEmailException;
import gr.abiss.calipso.userDetails.util.SecurityUtil;
import gr.abiss.calipso.userDetails.util.SimpleUserDetailsConfig;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.security.crypto.keygen.StringKeyGenerator;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.connect.UserProfile;
import org.springframework.social.connect.web.SignInAdapter;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.social.linkedin.api.LinkedIn;
import org.springframework.social.linkedin.api.LinkedInProfile;
import org.springframework.social.linkedin.api.impl.LinkedInTemplate;
import org.springframework.social.security.SocialUserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.context.request.NativeWebRequest;


//@Named("userDetailsService")
@Transactional(readOnly = true)
public class UserDetailsServiceImpl implements UserDetailsService,
		org.springframework.security.core.userdetails.UserDetailsService,
		org.springframework.social.security.SocialUserDetailsService,
		ConnectionSignUp, SignInAdapter {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

	private UserDetailsConfig userDetailsConfig = new SimpleUserDetailsConfig();

	private StringKeyGenerator keyGenerator = KeyGenerators.string();

	private UserService userService;
	
	@Autowired(required = false)
	public void setUserDetailsConfig(UserDetailsConfig userDetailsConfig) {
		this.userDetailsConfig = userDetailsConfig;
	}

	@Autowired(required = true)
	@Qualifier("userService") // somehow required for CDI to work on 64bit JDK?
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	@Override
	@Transactional(readOnly = false)
	public void updateLastLogin(ICalipsoUserDetails u){
		this.userService.updateLastLogin(u);
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.springframework.security.core.userdetails.UserDetailsService#loadUserByUsername(java.lang.String)
	 */
	@Override
	public ICalipsoUserDetails loadUserByUsername(
			String findByUsernameOrEmail) throws UsernameNotFoundException {
		ICalipsoUserDetails userDetails = null;

		if(LOGGER.isDebugEnabled()){
			LOGGER.debug("loadUserByUsername using: " + findByUsernameOrEmail);
		}
		User user = this.userService.findByUserNameOrEmail(findByUsernameOrEmail);


		if(LOGGER.isDebugEnabled()){
			LOGGER.debug("loadUserByUsername user: " + user);
		}

		userDetails = UserDetails.fromUser(user);

		if(LOGGER.isDebugEnabled()){
			LOGGER.debug("loadUserByUsername returns userDetails: " + userDetails
					+ ",	 with roles: " + userDetails.getAuthorities());
		}
		if (user == null) {
			throw new UsernameNotFoundException("Could not match username: " + findByUsernameOrEmail);
		}
		
		
		return userDetails;
	}

	/**
	 * {@inheritDoc}
	 */
	@Transactional(readOnly = false)
	@Override
	public ICalipsoUserDetails create(final ICalipsoUserDetails tryUserDetails) {

		if(LOGGER.isDebugEnabled()){
			LOGGER.debug("create, Trying to login with: "+tryUserDetails);
		}
		ICalipsoUserDetails userDetails = null;
		if (tryUserDetails != null) {
			try {
				String usernameOrEmail = tryUserDetails.getUsername();
				if (StringUtils.isBlank(usernameOrEmail)) {
					usernameOrEmail = tryUserDetails.getEmail();
				}
				String password = tryUserDetails.getPassword();
				// TODO
				Map<String, String> metadata = tryUserDetails.getMetadata();

				// make sure we have credentials to send
				if (StringUtils.isNotBlank(usernameOrEmail)
						&& StringUtils.isNotBlank(password)) {

					// ask for the corresponding persisted user
					User user = this.userService
							.findByCredentials(usernameOrEmail, password,
									metadata);
					if(LOGGER.isDebugEnabled()){
						LOGGER.debug("create, Matched local user: "+user);
					}
					if(user != null && user.getId() != null){
						if(LOGGER.isDebugEnabled()){
							LOGGER.debug("create, Creating user details from user...");
						}
						// convert to UserDetails if not null
						userDetails = UserDetails.fromUser(user);
					}
					
				}
			} catch (RuntimeException e) {
				LOGGER.error("create, failed creating user details object", e);
			}

		}
		return userDetails;
	}

//	@Override
//	@Transactional(readOnly = false)
//	public ICalipsoUserDetails confirmPrincipal(String confirmationToken) {
//		LOGGER.debug("confirmPrincipal confirmationToken: " +  confirmationToken);
//		Assert.notNull(confirmationToken);
//		ICalipsoUserDetails userDetails = null;
//		User user = this.userService.confirmPrincipal(confirmationToken);
//		// convert to UserDetals if not null
//		userDetails = UserDetails.fromUser(user);
//		userDetails.setNotificationCount(this.baseNotificationService.countUnseen(userDetails));
//		if(LOGGER.isDebugEnabled()){
//			LOGGER.debug("confirmPrincipal returning loggedInUserDetails: " +  userDetails);
//		}
//		return userDetails;
//	}

	@Override
	@Transactional(readOnly = false)
	public void handlePasswordResetRequest(String usernameOrEmail) {
		this.userService.handlePasswordResetRequest(usernameOrEmail);
	}

	@Override
	@Transactional(readOnly = false)
	public ICalipsoUserDetails resetPassword(ICalipsoUserDetails userDetails) {
		String userNameOrEmail = userDetails.getEmailOrUsername();
		String token = userDetails.getResetPasswordToken();
		String newPassword = userDetails.getPassword();
		Assert.notNull(userNameOrEmail);
		User user = this.userService.handlePasswordResetToken(
				userNameOrEmail, token, newPassword);
		if (user == null) {
			throw new UsernameNotFoundException("Could not match username: " + userNameOrEmail);
		}
		user.getCredentials().setResetPasswordToken(null);
		user.getCredentials().setPassword(newPassword);
		userDetails = UserDetails.fromUser(user);
		// LOGGER.info("create returning loggedInUserDetails: " +
		// loggedInUserDetails);
		return userDetails;
	}

	@Override
	public ICalipsoUserDetails getPrincipal() {
		return SecurityUtil.getPrincipal();
	}

	@Override
	public User getPrincipalLocalUser() {
		ICalipsoUserDetails principal = getPrincipal();
		User user = null;
		if (principal != null) {
			String username = principal.getUsername();
			if(StringUtils.isBlank(username)){
				username = principal.getEmail();
			}
			if(StringUtils.isNotBlank(username) && !"anonymous".equals(username)){
				user = this.userService.findByUserNameOrEmail(username);
			}
		}

		if(LOGGER.isDebugEnabled()){
			LOGGER.debug("getPrincipalUser, user: " + user);
		}
		return user;
	}

	@Override
	@Transactional(readOnly = false)
	public ICalipsoUserDetails update(ICalipsoUserDetails resource) {
		User user = this.userService.changePassword(resource.getEmailOrUsername(), resource.getCurrentPassword(), resource.getPassword(), resource.getPasswordConfirmation());
		return UserDetails.fromUser(user);
	}

	@Override
	public void delete(ICalipsoUserDetails resource) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void delete(String id) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteAll() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteAllWithCascade() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ICalipsoUserDetails findById(String id) {
		// LOGGER.info("findById: " + id);
		throw new UnsupportedOperationException();
	}

	@Override
	public List<ICalipsoUserDetails> findAll() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Page<ICalipsoUserDetails> findAll(Pageable pageRequest) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Long count() {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see org.springframework.social.security.SocialUserDetailsService#loadUserByUserId(java.lang.String)
	 */
	@Override
	public SocialUserDetails loadUserByUserId(String userId) throws UsernameNotFoundException, DataAccessException {
		ICalipsoUserDetails userDetails = null;

		// LOGGER.info("loadUserByUserId using: " + userId);
		User user = this.userService.findByUserNameOrEmail(userId);

		// LOGGER.info("loadUserByUserId user: " + user);
		if (user != null) {
			// LOGGER.info("loadUserByUserId about to copy user.roles: " +
			// user.getRoles());
			// Role userRole = new Role(Role.ROLE_USER);
			// user.addRole(userRole);
			userDetails = UserDetails.fromUser(user);
		}

		if (user == null) {
			throw new UsernameNotFoundException("Could not match user id: " + userId);
		}
		return userDetails;
	}
	
	/**
	 * @see org.springframework.social.connect.ConnectionSignUp#execute(org.springframework.social.connect.Connection)
	 */
	@Override
	@Transactional(readOnly = false)
	public String execute(Connection<?> connection) {
		
		UserProfile profile = connection.fetchUserProfile();

		String socialUsername = profile.getUsername();
		String socialName = profile.getName();
		String socialEmail = profile.getEmail();
		String socialFirstName = profile.getFirstName();
		String socialLastName = profile.getLastName();

		User user = this.getPrincipalLocalUser();
		
		if (!StringUtils.isBlank(socialEmail)) {
			user = userService.findByUserNameOrEmail(socialEmail);
			// 

			if (user == null) {
				if(LOGGER.isDebugEnabled()){
					LOGGER.debug("ConnectionSignUp#execute, Email did not match an local user, trying to create one");
				}

				user = new User();
				user.setEmail(socialEmail);
				user.setUsername(socialEmail);
				user.setFirstName(socialFirstName);
				user.setLastName(socialLastName);
				try {
					user = userService.createForImplicitSignup(user);
					
					//username = user.getUsername();
				} catch (DuplicateEmailException e) {
					LOGGER.error("ConnectionSignUp#executeError while implicitly registering user", e);
				}

			}
		}
		else {
			if(LOGGER.isDebugEnabled()){
				LOGGER.debug("ConnectionSignUp#execute, Social email was not accessible, unable to implicitly sign in user");
			}
		}
		//userService.createAccount(account);
		String result = user != null && user.getId() != null ? user.getId().toString() : null;
		if(LOGGER.isDebugEnabled()){
			LOGGER.debug("ConnectionSignUp#execute, returning result: "+result); 
		}
		return result;
	}

	/**
	 *  {@inheritDoc}
	 * @see org.springframework.social.connect.web.SignInAdapter#signIn(java.lang.String, org.springframework.social.connect.Connection, org.springframework.web.context.request.NativeWebRequest)
	 */
	@Override
	public String signIn(String userId, Connection<?> connection, NativeWebRequest request) {
		LOGGER.info("signIn, userId: " + userId);

		User user = this.userService.findById(userId);
		if(user == null){
			user = this.userService.findByUserNameOrEmail(userId);
		}
		//if(LOGGER.isDebugEnabled()){
			LOGGER.info("SignInAdapter#signIn userId: " + userId + ", connection: " + connection.getKey() + ", mached user: " + user);
		//}
		if(user != null){
			SecurityUtil.login((HttpServletRequest) request.getNativeRequest(), (HttpServletResponse) request.getNativeResponse(), user, this.userDetailsConfig, this);
		}
		return null;
	}

	@Override
	public ICalipsoUserDetails createForImplicitSignup(
			User user) throws DuplicateEmailException {
		LOGGER.info("createForImplicitSignup, user: " + user);
		ICalipsoUserDetails userDetails = UserDetails
				.fromUser(this.userService.createForImplicitSignup(user));
		return userDetails;
	}

	@Override
	public Iterable<ICalipsoUserDetails> findByIds(Set<String> ids) {
		throw new UnsupportedOperationException();
	}

}