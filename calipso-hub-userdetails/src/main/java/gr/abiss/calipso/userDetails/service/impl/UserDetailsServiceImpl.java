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
import gr.abiss.calipso.userDetails.model.UserDetails;
import gr.abiss.calipso.userDetails.service.UserDetailsService;
import gr.abiss.calipso.userDetails.util.DuplicateEmailException;
import gr.abiss.calipso.userDetails.util.SecurityUtil;
import gr.abiss.calipso.userDetails.util.SimpleUserDetailsConfig;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.Cookie;
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
import org.springframework.security.crypto.codec.Base64;
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
		org.springframework.security.core.userdetails.UserDetailsService, org.springframework.social.security.SocialUserDetailsService,
		ConnectionSignUp, SignInAdapter {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

	private UserDetailsConfig userDetailsConfig = new SimpleUserDetailsConfig();

	private StringKeyGenerator keyGenerator = KeyGenerators.string();

	private LocalUserService<? extends Serializable, ? extends LocalUser> localUserService;
	
	@Autowired(required = false)
	public void setUserDetailsConfig(UserDetailsConfig userDetailsConfig) {
		this.userDetailsConfig = userDetailsConfig;
	}

	@Autowired(required = true)
	public void setLocalUserService(
			LocalUserService<? extends Serializable, ? extends LocalUser> localUserService) {
		this.localUserService = localUserService;
	}

	@Override
	public UserDetails loadUserByUsername(
			String findByUserNameOrEmail) throws UsernameNotFoundException {
		gr.abiss.calipso.userDetails.model.UserDetails userDetails = null;

		// LOGGER.info("loadUserByUsername using: " + findByUserNameOrEmail);
		LocalUser user = this.localUserService.findByUserNameOrEmail(findByUserNameOrEmail);

		// LOGGER.info("loadUserByUsername user: " + user);

		userDetails = gr.abiss.calipso.userDetails.model.UserDetails
				.fromUser(user);

		// LOGGER.info("loadUserByUsername returns userDetails: " + userDetails
		// + ",	 with roles: " + userDetails.getAuthorities());
		if (user == null) {
			throw new UsernameNotFoundException("Could not match username: " + findByUserNameOrEmail);
		}
		return userDetails;
	}

	@Transactional(readOnly = false)
	@Override
	public gr.abiss.calipso.userDetails.model.UserDetails create(gr.abiss.calipso.userDetails.model.UserDetails loggedInUserDetails) {
		String userNameOrEmail = loggedInUserDetails.getUserName();
		if (StringUtils.isBlank(userNameOrEmail)) {
			userNameOrEmail = loggedInUserDetails.getEmail();
		}

		// LOGGER.info("create loggedInUserDetails: " + loggedInUserDetails);
		LocalUser orincipal = this.localUserService.findByCredentials(
				userNameOrEmail, loggedInUserDetails.getUserPassword(),
				loggedInUserDetails.getMetadata());

		// LOGGER.info("create principal: " + orincipal +
		// ", loggedInUserDetails: " + loggedInUserDetails);

		loggedInUserDetails = UserDetails.fromUser(orincipal);

		// LOGGER.info("create returning loggedInUserDetails: " +
		// loggedInUserDetails);
		return loggedInUserDetails;
	}

	@Override
	@Transactional(readOnly = false)
	public gr.abiss.calipso.userDetails.model.UserDetails confirmPrincipal(String confirmationToken) {
		Assert.notNull(confirmationToken);
		gr.abiss.calipso.userDetails.model.UserDetails loggedInUserDetails = null;
		LocalUser localUser = this.localUserService.confirmPrincipal(confirmationToken);

		loggedInUserDetails = UserDetails.fromUser(localUser);

		// LOGGER.info("create returning loggedInUserDetails: " +
		// loggedInUserDetails);
		return loggedInUserDetails;
	}

	@Override
	@Transactional(readOnly = false)
	public void handlePasswordResetRequest(String userNameOrEmail) {
		this.localUserService.handlePasswordResetRequest(userNameOrEmail);
	}

	@Override
	@Transactional(readOnly = false)
	public gr.abiss.calipso.userDetails.model.UserDetails resetPasswordAndLogin(String userNameOrEmail, String token, String newPassword) {
		Assert.notNull(userNameOrEmail);
		gr.abiss.calipso.userDetails.model.UserDetails loggedInUserDetails = null;
		LocalUser user = this.localUserService.handlePasswordResetToken(userNameOrEmail, token, newPassword);
		if (user == null) {
			throw new UsernameNotFoundException("Could not match username: " + userNameOrEmail);
		}
		user.setConfirmationToken(null);
		user.setUserPassword(newPassword);
		loggedInUserDetails = gr.abiss.calipso.userDetails.model.UserDetails.fromUser(user);

		// LOGGER.info("create returning loggedInUserDetails: " +
		// loggedInUserDetails);
		return loggedInUserDetails;
	}

	@Override
	public gr.abiss.calipso.userDetails.model.UserDetails getRemembered(HttpServletRequest request) {
		gr.abiss.calipso.userDetails.model.UserDetails resource = null;

		Cookie tokenCookie = null;
		Cookie[] cookies = request.getCookies();

		for (int i = 0; i < cookies.length; i++) {
			tokenCookie = cookies[i];
			if (tokenCookie.getName().equals(this.userDetailsConfig.getCookiesBasicAuthTokenName())) {
				String token = tokenCookie.getValue();
				if (StringUtils.isNotBlank(token)) {
					token = new String(Base64.decode(token.getBytes()));
					// LOGGER.info("Request contained token: " + token);
					if (token.indexOf(':') > 0) {
						String[] parts = token.split(":");
						// resource.setUserName(parts[0]);
						// resource.setUserPassword(parts[1]);
						LocalUser localUser = this.localUserService
								.findByCredentials(parts[0], parts[1], null);
						resource = UserDetails.fromUser(localUser);
						// TODO
					} else {
						LOGGER.warn("Invalid token received: " + token);
					}
				}
				break;
			}
		}
		return resource != null ? resource : new UserDetails();
	}

	@Override
	public gr.abiss.calipso.userDetails.model.UserDetails update(gr.abiss.calipso.userDetails.model.UserDetails resource) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void delete(gr.abiss.calipso.userDetails.model.UserDetails resource) {
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
	public gr.abiss.calipso.userDetails.model.UserDetails findById(String id) {
		// LOGGER.info("findById: " + id);
		throw new UnsupportedOperationException();
	}

	@Override
	public List<gr.abiss.calipso.userDetails.model.UserDetails> findAll() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Page<gr.abiss.calipso.userDetails.model.UserDetails> findAll(Pageable pageRequest) {
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
		SocialUserDetails userDetails = null;

		// LOGGER.info("loadUserByUserId using: " + userId);
		LocalUser user = this.localUserService.findByUserNameOrEmail(userId);

		// LOGGER.info("loadUserByUserId user: " + user);
		if (user != null) {
			// LOGGER.info("loadUserByUserId about to copy user.roles: " +
			// user.getRoles());
			// Role userRole = new Role(Role.ROLE_USER);
			// user.addRole(userRole);
			userDetails = gr.abiss.calipso.userDetails.model.UserDetails.fromUser(user);

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
    public String execute(Connection<?> connection) {
		// LOGGER.debug("ConnectionSignUp#execute");
		String localUserName = null;
		String accessToken = connection.createData().getAccessToken();
		UserProfile profile = connection.fetchUserProfile();
		ConnectionData data = connection.createData();

		String socialUsername = profile.getUsername();
		String socialName = profile.getName();
		String socialEmail = profile.getEmail();
		String socialFirstName = profile.getFirstName();
		String socialLastName = profile.getLastName();

		LOGGER.debug("ConnectionSignUp#execute, profile: " + profile + 
				", data: " + data +
				", socialUsername: " + socialUsername + 
				", socialName: " + socialName +
				", socialEmail: " + socialEmail +
				", socialFirstName: " + socialFirstName +
				", socialLastName: " + socialLastName+
				", accessToken: "+accessToken);

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
				// LOGGER.debug("Email matches existing local user, no need to create one");
				localUserName = user.getUserName();
			} else {
				// LOGGER.debug("Email did not match an local user, trying to create one");

				if (localUserService.findByUserNameOrEmail(socialUsername) != null) {
					// LOGGER.debug("The social account username is taken, will generate one using increment suffix");
					int increment = 1;
					for (int i = 0; localUserService.findByUserNameOrEmail(socialUsername + i) != null; i++) {
						increment++;
					}
					socialUsername = socialUsername + increment;
				}
				user = new gr.abiss.calipso.userDetails.model.SimpleLocalUser();
				user.setActive(true);
				user.setEmail(socialEmail);
				user.setUserName(socialUsername);
				user.setFirstName(socialFirstName);
				user.setLastName(socialLastName);
				user.setUserPassword(UUID.randomUUID().toString());
				try {
					localUserService.createForImplicitSignup(user);
					localUserName = user.getUserName();
				} catch (DuplicateEmailException e) {
					LOGGER.error("Error while implicitly registering user", e);
				}

			}
		}
		else {
			LOGGER.debug("Social email was not accessible, unable to implicitly sign in user");
		}
		//localUserService.createAccount(account);
		return localUserName;
	}

	/**
	 * @see org.springframework.social.connect.web.SignInAdapter#signIn(java.lang.String, org.springframework.social.connect.Connection, org.springframework.web.context.request.NativeWebRequest)
	 */
	@Override
	public String signIn(String userId, Connection<?> connection, NativeWebRequest request) {
		LocalUser user = this.localUserService.findByUserNameOrEmail(userId);
		LOGGER.debug("SignInAdapter#signIn userId: " + userId + ", connection: " + connection.getDisplayName() + ", mached user: " + user);
		SecurityUtil.login((HttpServletRequest) request.getNativeRequest(), (HttpServletResponse) request.getNativeResponse(), user, this.userDetailsConfig);
		return null;
	}

	@Override
	public gr.abiss.calipso.userDetails.model.UserDetails createForImplicitSignup(
			LocalUser localUser) throws DuplicateEmailException {
		return gr.abiss.calipso.userDetails.model.UserDetails
				.fromUser(this.localUserService
						.createForImplicitSignup(localUser));
	}

}