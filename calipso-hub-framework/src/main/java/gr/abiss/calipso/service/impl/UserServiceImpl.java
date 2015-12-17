/**
 * Copyright (c) 2007 - 2013 www.Abiss.gr
 *
 * This file is part of Calipso, a software platform by www.Abiss.gr.
 *
 * Calipso is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Calipso is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Calipso. If not, see http://www.gnu.org/licenses/agpl.html
 */
package gr.abiss.calipso.service.impl;

import gr.abiss.calipso.model.Role;
import gr.abiss.calipso.model.User;
import gr.abiss.calipso.model.interfaces.Metadatum;
import gr.abiss.calipso.model.metadata.UserMetadatum;
import gr.abiss.calipso.repository.RoleRepository;
import gr.abiss.calipso.repository.UserRepository;
import gr.abiss.calipso.service.EmailService;
import gr.abiss.calipso.service.UserService;
import gr.abiss.calipso.userDetails.integration.LocalUser;
import gr.abiss.calipso.userDetails.integration.LocalUserService;
import gr.abiss.calipso.userDetails.model.ICalipsoUserDetails;
import gr.abiss.calipso.userDetails.util.DuplicateEmailException;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.security.crypto.keygen.StringKeyGenerator;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

//@Named("userService")
public class UserServiceImpl extends GenericEntityServiceImpl<User, String, UserRepository> 
	implements UserService {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

	private final StringKeyGenerator generator = KeyGenerators.string();
	private RoleRepository roleRepository;

	@Inject
	public void setRoleRepository(RoleRepository roleRepository) {
		this.roleRepository = roleRepository;
	}
	@Override
	@Inject
	public void setRepository(UserRepository userRepository) {
		super.setRepository(userRepository);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateLastLogin(ICalipsoUserDetails u){
		this.repository.updateLastLogin(u.getId());
	}

	/**
	 * {@inheritDoc}
	 * @see gr.abiss.calipso.userDetails.integration.LocalUserService#findByCredentials(java.lang.String, java.lang.String, java.util.Map)
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public User findByCredentials(String userNameOrEmail, String password, Map metadata) {
		if(LOGGER.isDebugEnabled()) LOGGER.debug("findByCredentials, userNameOrEmail: " + userNameOrEmail + ", password: " + password + ", metadata: " + metadata);
		User user = null;
		try {
			user = this.repository.findByCredentials(userNameOrEmail, password);
			LOGGER.error("findByCredentials: matched user: "+user);
			if (user != null) {
				if (!CollectionUtils.isEmpty(metadata)) {
					List<Metadatum> saved = this.repository.addMetadata(user.getId(), metadata);
					for (Metadatum meta : saved) {
						user.addMetadatum((UserMetadatum) meta);
					}

				}
			}
		} catch (RuntimeException e) {
			LOGGER.error("failed finding user by credentials", e);
		}
		return user;
	}

	@Override
	@Transactional(readOnly = false)
	public User create(User resource) {

		LOGGER.info("create user: " + resource);
		Role userRole = roleRepository.findByName(Role.ROLE_USER);
		resource.addRole(userRole);
		ICalipsoUserDetails currentPrincipal = this.getPrincipal();
		if(!resource.getActive() 
				|| currentPrincipal == null 
				|| (!currentPrincipal.isAdmin() && !currentPrincipal.isSiteAdmin())){
			LOGGER.info("create, forcing active: false");
			resource.setActive(false);
			String token = generator.generateKey();
			LOGGER.info("creating user, confirmation token: " + token);
			resource.setResetPasswordToken(token);
		}
		LOGGER.info("create, user: " + resource);
		resource = super.create(resource);
		//roleRepository.save(userRole);
		LOGGER.info("create, created user: " + resource);

		if (resource != null && !resource.getActive()) {
			try {
				LOGGER.info("Sending account confirmation email...");
				emailService.sendAccountConfirmation(resource);
				LOGGER.info("Account confirmation email sent");
			} catch (MessagingException e) {
				LOGGER.error("Could not create account confirmation email", e);
			}
		}
		LOGGER.info("created user: " + resource);
		return resource;
	}


	@Override
	@Transactional(readOnly = false)
	public User createActive(User resource) {
		LOGGER.info("createActive, user: " + resource);
		resource.setActive(true);
		Role userRole = roleRepository.findByName(Role.ROLE_USER);
		resource.addRole(userRole);

		User user = repository.save(resource);
		roleRepository.save(userRole);
		return user;
	}

	@Override
	@Transactional(readOnly = false)
	public User handlePasswordResetToken(String userNameOrEmail, String token, String newPassword) {
		Assert.notNull(userNameOrEmail);
		User user = this.findByUserNameOrEmail(userNameOrEmail);
		if (user == null) {
			throw new UsernameNotFoundException("Could not match username: " + userNameOrEmail);
		}
		user.setResetPasswordToken(null);
		user.setPassword(newPassword);
		user.setActive(true);
		user = this.update(user);

		LOGGER.info("handlePasswordResetToken returning local user: " + user);
		return user;
	}

	@Override
	@Transactional(readOnly = false)
	public void handlePasswordResetRequest(String userNameOrEmail) {
		Assert.notNull(userNameOrEmail);
		User user = this.findByUserNameOrEmail(userNameOrEmail);
		if (user == null || !user.getActive()) {
			throw new UsernameNotFoundException("Could not match username to an active user: " + userNameOrEmail);
		}
		// keep any existing token
		if(user.getResetPasswordToken() == null){
			user.setResetPasswordToken(this.generator.generateKey());
			user = this.userRepository.save(user);
		}
		try {
			emailService.sendPasswordResetLink(user);
		} catch (MessagingException e) {
			throw new RuntimeException("Could not create password reset email", e);
		}
	}


//	@Override
//	@Transactional(readOnly = false)
//	public User confirmPrincipal(String confirmationToken) {
//		Assert.notNull(confirmationToken);
//		//LoggedInUserDetails loggedInUserDetails = new LoggedInUserDetails();
//		User original = this.userRepository.findByConfirmationToken(confirmationToken);
//		if (original != null) {
//			// enable and update user
//			original.setResetPasswordToken(null);
//			original = this.userRepository.save(original);
//		} else {
//			LOGGER.warn("Could not find any user matching confirmation token: " + confirmationToken);
//		}
//
//		LOGGER.info("create returning local user: " + original);
//		return original;
//	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public User findByUserNameOrEmail(String userNameOrEmail) {
		return this.repository.findByUsernameOrEmail(userNameOrEmail);
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = false)
	public User createForImplicitSignup(LocalUser userAccountData) throws DuplicateEmailException {
		LOGGER.info("createForImplicitSignup, localUser: " + userAccountData);
		User user = new User();
		user.setEmail(userAccountData.getEmail());
		user.setUsername(userAccountData.getUsername());
		user.setFirstName(userAccountData.getFirstName());
		user.setLastName(userAccountData.getLastName());
		user.setPassword(userAccountData.getPassword());
		User existing = this.repository.findByUsernameOrEmail(user.getEmail());
		if(existing == null){
			existing = this.repository.findByUsernameOrEmail(user.getUsername());
		}
		
//		if (this.repository.findByUsernameOrEmail(user.getUsername()) != null) {
//			throw new DuplicateEmailException("Email address exists: " + userAccountData.getEmail());
//		}
//		if (this.repository.findByUsernameOrEmail(user.getUsername()) != null) {
//			throw new DuplicateEmailException("Username exists: " + userAccountData.getEmail());
//		}
//
//		if(LOGGER.isDebugEnabled()){
//			LOGGER.debug("createForImplicitSignup returning local user: " + user);
//		}
		return existing != null ? existing : createActive(user);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = false)
	public User changePassword(String userNameOrEmail, String oldPassword, String newPassword,
			String newPasswordConfirm) {
		
		// make sure we have all params
		String[] params = {userNameOrEmail, oldPassword, newPassword, newPasswordConfirm};
		Assert.noNullElements(params, "Failed updating user pass: Username/email, old password, new password and new password confirmation must be provided ");
		
		// make sure new password and confirm match
		Assert.isTrue(newPassword.equals(newPasswordConfirm), "Failed updating user pass: New password and new password confirmation must be equal");
		
		// make sure a user matching the credentials is found
		User u = this.findByCredentials(userNameOrEmail, oldPassword, null);
		Assert.notNull(u, "Failed updating user pass: A user could not be found with the given credentials");
		
		// update password and return user
		u.setPassword(newPassword);
		u.setLastPassWordChangeDate(new Date());
		u = this.update(u);
		return u;
	}


}