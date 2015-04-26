/**
 *
 *
 * Copyright (c) 2007 - 2013 www.Abiss.gr
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package gr.abiss.calipso.userDetails.service.impl;

import gr.abiss.calipso.userDetails.integration.LocalUser;
import gr.abiss.calipso.userDetails.integration.LocalUserService;
import gr.abiss.calipso.userDetails.integration.UserDetailsConfig;
import gr.abiss.calipso.userDetails.model.ICalipsoUserDetails;
import gr.abiss.calipso.userDetails.model.SimpleLocalUser;
import gr.abiss.calipso.userDetails.model.UserDetails;
import gr.abiss.calipso.userDetails.service.UserDetailsService;
import gr.abiss.calipso.notification.service.BaseNotificationsService;

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
	
	private BaseNotificationsService baseNotificationService;
	
	@Autowired(required = false)
	public void setUserDetailsConfig(UserDetailsConfig userDetailsConfig) {
		this.userDetailsConfig = userDetailsConfig;
	}

	@Autowired(required = true)
	public void setLocalUserService(
			LocalUserService<? extends Serializable, ? extends LocalUser> localUserService) {
		this.localUserService = localUserService;
	}

	@Autowired(required = true)
	public void setBaseNotificationsService(BaseNotificationsService baseNotificationsService) {
		this.baseNotificationService = baseNotificationsService;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.springframework.security.core.userdetails.UserDetailsService#loadUserByUsername(java.lang.String)
	 */
	@Override
	public ICalipsoUserDetails loadUserByUsername(
			String findByUserNameOrEmail) throws UsernameNotFoundException {
		ICalipsoUserDetails userDetails = null;

		if(LOGGER.isDebugEnabled()){
			LOGGER.debug("loadUserByUsername using: " + findByUserNameOrEmail);
		}
		LocalUser user = this.localUserService.findByUserNameOrEmail(findByUserNameOrEmail);


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
			throw new UsernameNotFoundException("Could not match username: " + findByUserNameOrEmail);
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
				String userNameOrEmail = tryUserDetails.getUsername();
				if (StringUtils.isBlank(userNameOrEmail)) {
					userNameOrEmail = tryUserDetails.getEmail();
				}
				String password = tryUserDetails.getPassword();
				// TODO
				Map<String, String> metadata = tryUserDetails.getMetadata();

				// make sure we have credentials to send
				if (StringUtils.isNotBlank(userNameOrEmail)
						&& StringUtils.isNotBlank(password)) {

					// ask for the corresponding persisted user
					LocalUser localUser = this.localUserService
							.findByCredentials(userNameOrEmail, password,
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


		if(LOGGER.isDebugEnabled()){
			LOGGER.debug("create, testing returning null, userDetails: " + userDetails);
		}
		return userDetails;
	}

	@Override
	@Transactional(readOnly = false)
	public ICalipsoUserDetails confirmPrincipal(String confirmationToken) {
		Assert.notNull(confirmationToken);
		ICalipsoUserDetails userDetails = null;
		LocalUser localUser = this.localUserService.confirmPrincipal(confirmationToken);
		// convert to UserDetals if not null
		userDetails = UserDetails.fromUser(localUser);
		userDetails.setNotificationCount(this.baseNotificationService.countUnseen(userDetails));
		if(LOGGER.isDebugEnabled()){
			LOGGER.debug("confirmPrincipal returning loggedInUserDetails: " +  userDetails);
		}
		return userDetails;
	}

	@Override
	@Transactional(readOnly = false)
	public void handlePasswordResetRequest(String userNameOrEmail) {
		this.localUserService.handlePasswordResetRequest(userNameOrEmail);
	}

	@Override
	@Transactional(readOnly = false)
	public ICalipsoUserDetails resetPassword(String userNameOrEmail, String token, String newPassword) {
		Assert.notNull(userNameOrEmail);
		ICalipsoUserDetails userDetails = null;
		LocalUser localUser = this.localUserService.handlePasswordResetToken(
				userNameOrEmail, token, newPassword);
		if (localUser == null) {
			throw new UsernameNotFoundException("Could not match username: " + userNameOrEmail);
		}
		localUser.setConfirmationToken(null);
		localUser.setUserPassword(newPassword);
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
	public ICalipsoUserDetails update(ICalipsoUserDetails resource) {
		throw new UnsupportedOperationException();
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
		if(LOGGER.isDebugEnabled()){
			LOGGER.debug("ConnectionSignUp#execute, connection: "+connection);
		}
		String localUserName = null;
		String accessToken = connection.createData().getAccessToken();
		UserProfile profile = connection.fetchUserProfile();
		ConnectionData data = connection.createData();

		String socialUsername = profile.getUsername();
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

		if (!StringUtils.isBlank(socialEmail)) {
			// LOGGER.debug("ConnectionSignUp#execute, Social email accessible, looking for local user match");

			LocalUser user = localUserService.findByUserNameOrEmail(socialEmail);
			// 

			if (user != null) {
				if(LOGGER.isDebugEnabled()){
					LOGGER.debug("ConnectionSignUp#execute, Email matches existing local user, no need to create one");
				}
				localUserName = user.getUserName();
			} else {
				if(LOGGER.isDebugEnabled()){
					LOGGER.debug("ConnectionSignUp#execute, Email did not match an local user, trying to create one");
				}

				if (localUserService.findByUserNameOrEmail(socialUsername) != null) {
					if(LOGGER.isDebugEnabled()){
						LOGGER.debug("ConnectionSignUp#execute, The social account username is taken, will generate one using increment suffix");
					}
					int increment = 1;
					for (int i = 0; localUserService.findByUserNameOrEmail(socialUsername + i) != null; i++) {
						increment++;
					}
					socialUsername = socialUsername + increment;
				}
				user = new SimpleLocalUser();
				user.setActive(true);
				user.setEmail(socialEmail);
				user.setUserName(socialUsername);
				user.setFirstName(socialFirstName);
				user.setLastName(socialLastName);
				user.setUserPassword(UUID.randomUUID().toString());
				try {
					user = localUserService.createForImplicitSignup(user);
					
					localUserName = user.getUserName();
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
		if(LOGGER.isDebugEnabled()){
			LOGGER.debug("ConnectionSignUp#execute, returning localUserName: "+localUserName); 
		}
		return localUserName;
	}

	/**
	 *  {@inheritDoc}
	 * @see org.springframework.social.connect.web.SignInAdapter#signIn(java.lang.String, org.springframework.social.connect.Connection, org.springframework.web.context.request.NativeWebRequest)
	 */
	@Override
	public String signIn(String userId, Connection<?> connection, NativeWebRequest request) {
		LocalUser user = this.localUserService.findByUserNameOrEmail(userId);
		if(LOGGER.isDebugEnabled()){
			LOGGER.debug("SignInAdapter#signIn userId: " + userId + ", connection: " + connection.getDisplayName() + ", mached user: " + user);
		}
		if(user != null){
			SecurityUtil.login((HttpServletRequest) request.getNativeRequest(), (HttpServletResponse) request.getNativeResponse(), user, this.userDetailsConfig);
		}
		return null;
	}

	@Override
	public ICalipsoUserDetails createForImplicitSignup(
			LocalUser localUser) throws DuplicateEmailException {
		LOGGER.debug("createForImplicitSignup, localUser: " + localUser);
		ICalipsoUserDetails userDetails = UserDetails
				.fromUser(this.localUserService	.createForImplicitSignup(localUser));
		userDetails.setNotificationCount(this.baseNotificationService.countUnseen(userDetails));
		return userDetails;
	}

	//@Override
	public Iterable<UserDetails> findByIds(Set<String> ids) {
		throw new UnsupportedOperationException();
	}

}