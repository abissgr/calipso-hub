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

import gr.abiss.calipso.userDetails.integration.LocalUser;
import gr.abiss.calipso.userDetails.integration.LocalUserService;
import gr.abiss.calipso.userDetails.integration.UserDetailsConfig;
import gr.abiss.calipso.userDetails.model.ICalipsoUserDetails;
import gr.abiss.calipso.userDetails.model.SimpleLocalUser;
import gr.abiss.calipso.userDetails.model.UserDetails;
import gr.abiss.calipso.userDetails.service.UserDetailsService;
import gr.abiss.calipso.notification.service.BaseNotificationService;
import gr.abiss.calipso.userDetails.util.DuplicateEmailException;
import gr.abiss.calipso.userDetails.util.SecurityUtil;
import gr.abiss.calipso.userDetails.util.SimpleUserDetailsConfig;
import gr.abiss.calipso.userDetails.util.SocialMediaService;

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
import org.springframework.social.facebook.api.User;
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

	private LocalUserService<? extends Serializable, ? extends LocalUser> localUserService;
	
	private BaseNotificationService baseNotificationService;
	
	@Autowired(required = false)
	public void setUserDetailsConfig(UserDetailsConfig userDetailsConfig) {
		this.userDetailsConfig = userDetailsConfig;
	}

	@Autowired(required = true)
	@Qualifier("localUserService") // somehow required for CDI to work on 64bit JDK?
	public void setLocalUserService(
			LocalUserService<? extends Serializable, ? extends LocalUser> localUserService) {
		this.localUserService = localUserService;
	}

	@Autowired(required = true)
	public void setBaseNotificationsService(BaseNotificationService baseNotificationService) {
		this.baseNotificationService = baseNotificationService;
	}

	@Override
	@Transactional(readOnly = false)
	public void updateLastLogin(ICalipsoUserDetails u){
		this.localUserService.updateLastLogin(u);
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
		LocalUser user = this.localUserService.findByUserNameOrEmail(findByUsernameOrEmail);


		if(LOGGER.isDebugEnabled()){
			LOGGER.debug("loadUserByUsername user: " + user);
		}

		userDetails = UserDetails.fromUser(user);
		userDetails.setNotificationCount(this.baseNotificationService.countUnseen(userDetails));

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
					LocalUser localUser = this.localUserService
							.findByCredentials(usernameOrEmail, password,
									metadata);
					if(LOGGER.isDebugEnabled()){
						LOGGER.debug("create, Matched local user: "+localUser);
					}
					if(localUser != null && localUser.getId() != null){
						if(LOGGER.isDebugEnabled()){
							LOGGER.debug("create, Creating user details from localUser...");
						}
						// convert to UserDetails if not null
						userDetails = UserDetails.fromUser(localUser);
						userDetails.setNotificationCount(this.baseNotificationService.countUnseen(userDetails));
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
//		LocalUser localUser = this.localUserService.confirmPrincipal(confirmationToken);
//		// convert to UserDetals if not null
//		userDetails = UserDetails.fromUser(localUser);
//		userDetails.setNotificationCount(this.baseNotificationService.countUnseen(userDetails));
//		if(LOGGER.isDebugEnabled()){
//			LOGGER.debug("confirmPrincipal returning loggedInUserDetails: " +  userDetails);
//		}
//		return userDetails;
//	}

	@Override
	@Transactional(readOnly = false)
	public void handlePasswordResetRequest(String usernameOrEmail) {
		this.localUserService.handlePasswordResetRequest(usernameOrEmail);
	}

	@Override
	@Transactional(readOnly = false)
	public ICalipsoUserDetails resetPassword(ICalipsoUserDetails userDetails) {
		String userNameOrEmail = userDetails.getEmailOrUsername();
		String token = userDetails.getResetPasswordToken();
		String newPassword = userDetails.getPassword();
		Assert.notNull(userNameOrEmail);
		LocalUser localUser = this.localUserService.handlePasswordResetToken(
				userNameOrEmail, token, newPassword);
		if (localUser == null) {
			throw new UsernameNotFoundException("Could not match username: " + userNameOrEmail);
		}
		localUser.setResetPasswordToken(null);
		localUser.setPassword(newPassword);
		userDetails = UserDetails.fromUser(localUser);
		userDetails.setNotificationCount(this.baseNotificationService.countUnseen(userDetails));
		// LOGGER.info("create returning loggedInUserDetails: " +
		// loggedInUserDetails);
		return userDetails;
	}

	@Override
	public ICalipsoUserDetails getPrincipal() {
		return SecurityUtil.getPrincipal();
	}

	@Override
	public LocalUser getPrincipalLocalUser() {
		ICalipsoUserDetails principal = getPrincipal();
		LocalUser user = null;
		if (principal != null) {
			String username = principal.getUsername();
			if(StringUtils.isBlank(username)){
				username = principal.getEmail();
			}
			if(StringUtils.isNotBlank(username) && !"anonymous".equals(username)){
				user = this.localUserService.findByUserNameOrEmail(username);
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
		LocalUser user = this.localUserService.changePassword(resource.getEmailOrUsername(), resource.getCurrentPassword(), resource.getPassword(), resource.getPasswordConfirmation());
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
		LocalUser user = this.localUserService.findByUserNameOrEmail(userId);

		// LOGGER.info("loadUserByUserId user: " + user);
		if (user != null) {
			// LOGGER.info("loadUserByUserId about to copy user.roles: " +
			// user.getRoles());
			// Role userRole = new Role(Role.ROLE_USER);
			// user.addRole(userRole);
			userDetails = UserDetails.fromUser(user);
			userDetails.setNotificationCount(this.baseNotificationService.countUnseen(user));
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
		//if(LOGGER.isDebugEnabled()){
			LOGGER.info("ConnectionSignUp#execute, connection: "+connection);
		//}
		//String localUsername = null;
		String accessToken = connection.createData().getAccessToken();
		UserProfile profile = connection.fetchUserProfile();
		ConnectionData data = connection.createData();

		String socialUsername = profile.getUsername();
		if (StringUtils.isBlank(socialUsername)) {
			LOGGER.info("blank username for profile class: " + profile.getClass());
			Object api = connection.getApi();
			if (SocialMediaService.FACEBOOK.toString().equalsIgnoreCase(
					connection.createData().getProviderId())) {
				Facebook fbApi = new FacebookTemplate(accessToken);
				User fbProfile = fbApi.userOperations().getUserProfile();
				if (fbProfile != null) {
					socialUsername = fbProfile.getId();
					LOGGER.debug("ConnectionSignUp#execute, Got facebook id: " 	+ socialUsername);
				}
			}
			else if (SocialMediaService.LINKEDIN.toString().equalsIgnoreCase(
						connection.createData().getProviderId())) {
					LinkedIn liApi = new LinkedInTemplate(accessToken);
					LinkedInProfile liProfile = liApi.profileOperations().getUserProfile();
					if (liProfile != null) {
						socialUsername = liProfile.getId();
						LOGGER.debug("ConnectionSignUp#execute, Got linkedin id: " 	+ socialUsername);
					}
				}
		}
		String socialName = profile.getName();
		String socialEmail = profile.getEmail();
		String socialFirstName = profile.getFirstName();
		String socialLastName = profile.getLastName();

		if(LOGGER.isDebugEnabled()){
			LOGGER.debug("ConnectionSignUp#execute, profile: " + profile + 
				", data: " + data +
				", socialUsername: " + socialUsername + 
				", socialName: " + socialName +
				", socialEmail: " + socialEmail +
				", socialFirstName: " + socialFirstName +
				", socialLastName: " + socialLastName+
				", accessToken: "+accessToken);
		}
		// get email from github if empty
		//		if (StringUtils.isNullOrEmpty(socialEmail)) {
		//			Object api = connection.getApi();
		//			if (SocialMediaService.GITHUB.toString().equalsIgnoreCase(connection.createData().getProviderId())) {
		//				GitHub githubApi = new GitHubTemplate(accessToken);//(GitHub) api;
		//				GitHubUserProfile githubProfile = githubApi.userOperations().getUserProfile();
		//				LOGGER.debug("ConnectionSignUp#execute, Got github profile: " + githubProfile + ", authorized: " + githubApi.isAuthorized());
		//				if (githubProfile != null) {
		//					socialEmail = githubProfile.getEmail();
		//					LOGGER.debug("ConnectionSignUp#execute, Got github email: " + socialEmail);
		//				}
		//			}
		//		}
		LocalUser user = null;
		if (!StringUtils.isBlank(socialEmail)) {
			// LOGGER.debug("ConnectionSignUp#execute, Social email accessible, looking for local user match");

			user = localUserService.findByUserNameOrEmail(socialEmail);
			// 

			if (user != null) {
				if(LOGGER.isDebugEnabled()){
					LOGGER.debug("ConnectionSignUp#execute, Email matches existing local user, no need to create one");
				}
				//localUsername = user.getUsername();
			} else {
				if(LOGGER.isDebugEnabled()){
					LOGGER.debug("ConnectionSignUp#execute, Email did not match an local user, trying to create one");
				}

				user = new SimpleLocalUser();
				user.setActive(true);
				user.setEmail(socialEmail);
				user.setUsername(socialEmail);
				user.setFirstName(socialFirstName);
				user.setLastName(socialLastName);
				user.setPassword(UUID.randomUUID().toString());
				try {
					user = localUserService.createForImplicitSignup(user);
					
					//localUsername = user.getUsername();
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
		//localUserService.createAccount(account);
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

		LocalUser user = this.localUserService.findById(userId);
		if(user == null){
			user = this.localUserService.findByUserNameOrEmail(userId);
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
			LocalUser localUser) throws DuplicateEmailException {
		LOGGER.info("createForImplicitSignup, localUser: " + localUser);
		ICalipsoUserDetails userDetails = UserDetails
				.fromUser(this.localUserService	.createForImplicitSignup(localUser));
		userDetails.setNotificationCount(this.baseNotificationService.countUnseen(userDetails));
		return userDetails;
	}

	@Override
	public Iterable<ICalipsoUserDetails> findByIds(Set<String> ids) {
		throw new UnsupportedOperationException();
	}

}