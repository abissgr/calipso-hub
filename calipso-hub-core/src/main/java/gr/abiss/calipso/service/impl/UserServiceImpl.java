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

import gr.abiss.calipso.model.User;
import gr.abiss.calipso.model.interfaces.Metadatum;
import gr.abiss.calipso.model.metadata.UserMetadatum;
import gr.abiss.calipso.repository.UserRepository;
import gr.abiss.calipso.service.EmailService;
import gr.abiss.calipso.service.UserService;
import gr.abiss.calipso.userDetails.integration.LocalUser;
import gr.abiss.calipso.userDetails.integration.LocalUserService;
import gr.abiss.calipso.userDetails.util.DuplicateEmailException;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.security.crypto.keygen.StringKeyGenerator;
import org.springframework.util.CollectionUtils;

//@Named("userService")
public class UserServiceImpl extends AbstractServiceImpl<User, String, UserRepository> 
	implements UserService, LocalUserService<String, User> {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

	private final StringKeyGenerator generator = KeyGenerators.string();

	@Override
	@Inject
	public void setRepository(UserRepository userRepository) {
		super.setRepository(userRepository);
	}

	/**
	 * {@inheritDoc}
	 * @see gr.abiss.calipso.userDetails.integration.LocalUserService#findByCredentials(java.lang.String, java.lang.String, java.util.Map)
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public User findByCredentials(String userNameOrEmail, 	String password, Map metadata) {
		if(LOGGER.isDebugEnabled()) LOGGER.debug("findByCredentials, userNameOrEmail: " + userNameOrEmail + ", password: " + password + ", metadata: " + metadata);
		User user = null;
		try {
			user = this.repository.findByCredentials(userNameOrEmail, password);
			LOGGER.error("findByCredentials: matched user: "+user);
			if (user != null && !CollectionUtils.isEmpty(metadata)) {
				List<Metadatum> saved = this.repository.addMetadata(user.getId(), metadata);
				for (Metadatum meta : saved) {
					user.addMetadatum((UserMetadatum) meta);
				}

			}
		} catch (RuntimeException e) {
			LOGGER.error("failed finding user by credentials", e);
		}
		return user;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public User findByUserNameOrEmail(String userNameOrEmail) {
		return this.repository.findByUserNameOrEmail(userNameOrEmail);
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public User createForImplicitSignup(LocalUser user)
			throws DuplicateEmailException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public User confirmPrincipal(String confirmationToken) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void handlePasswordResetRequest(String userNameOrEmail) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public User handlePasswordResetToken(String userNameOrEmail,
			String token, String newPassword) {
		// TODO Auto-generated method stub
		return null;
	}

}