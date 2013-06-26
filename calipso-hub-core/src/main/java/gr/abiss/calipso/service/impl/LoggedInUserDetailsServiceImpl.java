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
 * You should have received a copy of the GNU General Public License
 * along with Calipso. If not, see http://www.gnu.org/licenses/agpl.html
 */
package gr.abiss.calipso.service.impl;

import gr.abiss.calipso.model.LoggedInUserDetails;
import gr.abiss.calipso.model.User;
import gr.abiss.calipso.repository.UserRepository;
import gr.abiss.calipso.service.EmailService;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.mail.MessagingException;

import org.h2.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.security.crypto.keygen.StringKeyGenerator;
import org.springframework.util.Assert;

@Named("userDetailsService")
public class LoggedInUserDetailsServiceImpl implements
		gr.abiss.calipso.service.LoggedInUserDetailsService,
		org.springframework.security.core.userdetails.UserDetailsService {
	private static final Logger LOGGER = LoggerFactory.getLogger(LoggedInUserDetailsServiceImpl.class);

	private final StringKeyGenerator generator = KeyGenerators.string();
	private UserRepository userRepository;
	private EmailService emailService;

	@Inject
	public void setEmailService(EmailService emailService) {
		this.emailService = emailService;
	}

	@Inject
	@Named("userRepository")
	public void setRepository(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String findByUserNameOrEmail) throws UsernameNotFoundException {
		org.springframework.security.core.userdetails.User userDetails = null;

		LOGGER.info("loadUserByUsername using: " + findByUserNameOrEmail);
		User user = this.userRepository.findByUserNameOrEmail(findByUserNameOrEmail);

		LOGGER.info("loadUserByUsername user: " + user);
		if (user != null) {
			LOGGER.info("loadUserByUsername about to copy user.roles: " + user.getRoles());
			// Role userRole = new Role(Role.ROLE_USER);
			// user.addRole(userRole);
			userDetails = new gr.abiss.calipso.model.acl.UserDetails(
					user.getUserName(), user.getUserPassword(),
					user.getActive(),
					user.getActive(), user.getActive(), user.getActive(), user.getRoles());
		}

		LOGGER.info("loadUserByUsername returns userDetails: " + userDetails + ", with roles: " + userDetails.getAuthorities());
		if (user == null) {
			throw new UsernameNotFoundException("Could not match username: " + findByUserNameOrEmail);
		}
		return userDetails;
	}

	@Override
	public LoggedInUserDetails create(LoggedInUserDetails loggedInUserDetails) {
		String userNameOrEmail = loggedInUserDetails.getUserName();
		if (StringUtils.isNullOrEmpty(userNameOrEmail)) {
			userNameOrEmail = loggedInUserDetails.getEmail();
		}

		User orincipal = this.userRepository.findByCredentials(userNameOrEmail, loggedInUserDetails.getUserPassword());

		LOGGER.info("create principal: " + orincipal + ", loggedInUserDetails: " + loggedInUserDetails);
		if (orincipal != null) {
			BeanUtils.copyProperties(orincipal, loggedInUserDetails);
			LoggedInUserDetails.initRoles(loggedInUserDetails, orincipal.getRoles());
		} else {
			loggedInUserDetails.setUserPassword(null);
		}

		LOGGER.info("create returning loggedInUserDetails: " + loggedInUserDetails);
		return loggedInUserDetails;
	}

	@Override
	public LoggedInUserDetails create(String confirmationToken) {
		Assert.notNull(confirmationToken);
		LoggedInUserDetails loggedInUserDetails = new LoggedInUserDetails();
		User original = this.userRepository.findByConfirmationToken(confirmationToken);
		if (original != null) {
			// enable and update user
			original.setConfirmationToken(null);
			original.setActive(true);
			original = this.userRepository.save(original);

			LOGGER.info("create principal: " + original + ", loggedInUserDetails: " + loggedInUserDetails);
			if (original != null) {
				BeanUtils.copyProperties(original, loggedInUserDetails);
				LoggedInUserDetails.initRoles(loggedInUserDetails, original.getRoles());
			} else {
				loggedInUserDetails.setUserPassword(null);
			}
		} else {
			LOGGER.warn("Could not find any user matching confirmation token: " + confirmationToken);
		}

		LOGGER.info("create returning loggedInUserDetails: " + loggedInUserDetails);
		return loggedInUserDetails;
	}

	@Override
	public void sendPasswordResetToken(String userNameOrEmail) {
		Assert.notNull(userNameOrEmail);
		User user = this.userRepository.findByUserNameOrEmail(userNameOrEmail);
		if (user == null) {
			throw new UsernameNotFoundException("Could not match username: " + userNameOrEmail);
		}
		// user.setResetPasswordToken(this.generator.generateKey());
		user = this.userRepository.save(user);
		try {
			emailService.sendPasswordResetLink(user);
		} catch (MessagingException e) {
			throw new RuntimeException("Could not create password reset email", e);
		}
	}

	@Override
	public LoggedInUserDetails resetPasswordAndLogin(String userNameOrEmail, String token, String newPassword) {
		Assert.notNull(userNameOrEmail);
		LoggedInUserDetails loggedInUserDetails = new LoggedInUserDetails();
		User user = this.userRepository.findByUserNameOrEmail(userNameOrEmail);
		if (user == null) {
			throw new UsernameNotFoundException("Could not match username: " + userNameOrEmail);
		}
		user.setConfirmationToken(null);
		user.setUserPassword(newPassword);
		user = this.userRepository.save(user);
		LOGGER.info("create principal: " + user + ", loggedInUserDetails: " + loggedInUserDetails);
		if (user != null) {
			BeanUtils.copyProperties(user, loggedInUserDetails);
			LoggedInUserDetails.initRoles(loggedInUserDetails, user.getRoles());
		} else {
			loggedInUserDetails.setUserPassword(null);
		}

		LOGGER.info("create returning loggedInUserDetails: " + loggedInUserDetails);
		return loggedInUserDetails;
	}

	@Override
	public LoggedInUserDetails update(LoggedInUserDetails resource) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void delete(LoggedInUserDetails resource) {
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
	public LoggedInUserDetails findById(String id) {
		LOGGER.info("findById: " + id);
		throw new UnsupportedOperationException();
	}

	@Override
	public List<LoggedInUserDetails> findAll() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Page<LoggedInUserDetails> findAll(Pageable pageRequest) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Long count() {
		throw new UnsupportedOperationException();
	}

}