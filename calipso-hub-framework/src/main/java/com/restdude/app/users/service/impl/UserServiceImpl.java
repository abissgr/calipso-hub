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
package com.restdude.app.users.service.impl;

import com.restdude.app.users.model.Role;
import com.restdude.app.users.model.User;
import com.restdude.app.users.model.UserCredentials;
import com.restdude.app.users.model.UserRegistrationCode;
import com.restdude.app.users.repository.RoleRepository;
import com.restdude.app.users.repository.UserCredentialsRepository;
import com.restdude.app.users.repository.UserRegistrationCodeRepository;
import com.restdude.app.users.repository.UserRepository;
import com.restdude.app.users.service.UserService;
import com.restdude.auth.userdetails.model.ICalipsoUserDetails;
import com.restdude.auth.userdetails.util.DuplicateEmailException;
import gr.abiss.calipso.model.dto.UserDTO;
import gr.abiss.calipso.model.dto.UserInvitationResultsDTO;
import gr.abiss.calipso.model.dto.UserInvitationsDTO;
import gr.abiss.calipso.model.interfaces.Metadatum;
import gr.abiss.calipso.model.metadata.UserMetadatum;
import gr.abiss.calipso.tiers.service.AbstractModelServiceImpl;
import gr.abiss.calipso.web.spring.BadRequestException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.security.crypto.keygen.StringKeyGenerator;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

//@Named("userService")
public class UserServiceImpl extends AbstractModelServiceImpl<User, String, UserRepository> 
	implements UserService {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);
	private static final String USERDTO_CLASS = UserDTO.class.getCanonicalName();

	private final StringKeyGenerator generator = KeyGenerators.string();
	
	private RoleRepository roleRepository;
	private UserCredentialsRepository credentialsRepository;
	private UserRegistrationCodeRepository userRegistrationCodeRepository;

	@Autowired
	public void setRoleRepository(RoleRepository roleRepository) {
		this.roleRepository = roleRepository;
	}


	@Autowired
	public void setCredentialsRepository(UserCredentialsRepository credentialsRepository) {
		this.credentialsRepository = credentialsRepository;
	}

	@Autowired
	public void setUserRegistrationCodeRepository(UserRegistrationCodeRepository userRegistrationCodeRepository) {
		this.userRegistrationCodeRepository = userRegistrationCodeRepository;
	}
	
	@Override
	@Transactional(readOnly = false)
	public void updateLastLogin(ICalipsoUserDetails u){
		this.repository.updateLastLogin(u.getId());
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public User findActiveByCredentials(String userNameOrEmail, String password, Map metadata) {
		LOGGER.debug("findByCredentials, userNameOrEmail: {}, password: {}", userNameOrEmail, password);
		
		User user = null;
		try {
			user = this.findActiveByCredentials(userNameOrEmail, password);
			LOGGER.debug("findByCredentials, user: {}", user);
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

		// note any credential info
		UserCredentials credentials = resource.getCredentials();
		resource.setCredentials(null);

		Role userRole = roleRepository.findByName(Role.ROLE_USER);
		resource.addRole(userRole);
		resource = super.create(resource);

		// init credentials if empty
		if (credentials == null) {
			credentials = new UserCredentials();
		}
		credentials.setActive(false);
		credentials.setUser(resource);
		credentials.setResetPasswordToken(generator.generateKey());

		// note any registration code info
		UserRegistrationCode code = credentials.getRegistrationCode();
		if (code != null) {
			code = this.userRegistrationCodeRepository.getOne(code.getId());
			if (!code.getAvailable()) {
				throw new BadRequestException("Invalid registration code");
			}
		}

		// attach credentials
		resource.setCredentials(this.credentialsRepository.save(credentials));

		// update code
		if (code != null) {
			code.setCredentials(resource.getCredentials());
			this.userRegistrationCodeRepository.save(code);
		}

		// sent email confirmation message
		emailService.sendAccountConfirmation(resource);

		return resource;
	}


	@Override
	@Transactional(readOnly = false)
	public void expireResetPasswordTokens() {
		// get a hibernate session suitable for read-only access to large datasets
		StatelessSession session = ((Session) this.repository.getEntityManager().getDelegate()).getSessionFactory().openStatelessSession();
		Date yesterday = DateUtils.addDays(new Date(), -1);
		
		// send email notifications for account confirmation tokens that expired
        org.hibernate.Query query = session.createQuery("SELECT new " + USERDTO_CLASS
				+ "(u.id, u.firstName, u.lastName,u.credentials.username, u.email, u.emailHash, u.avatarUrl) FROM User u "
				+ "WHERE u.credentials.password IS NULL and u.credentials.resetPasswordTokenCreated IS NOT NULL and u.credentials.resetPasswordTokenCreated  < :yesterday");
		query.setParameter("yesterday", yesterday);
        query.setFetchSize(Integer.valueOf(1000));
        query.setReadOnly(true);
        query.setLockMode("a", LockMode.NONE);
        ScrollableResults results = query.scroll(ScrollMode.FORWARD_ONLY);
        while (results.next()) {
        	UserDTO dto = (UserDTO) results.get(0);
            // TODO: send expiration email
            this.emailService.sendAccountConfirmationExpired(new User(dto));
        }
        results.close();
        session.close();
        
        // expire tokens, including password reset requests
        this.repository.expireResetPasswordTokens(yesterday);
	}
	
	@Override
	@Transactional(readOnly = false)
	public User createTest(User resource) {
		LOGGER.warn("createActive, credentials: {}", resource.getCredentials());

		// note any credential info
		UserCredentials credentials = resource.getCredentials();
		resource.setCredentials(null);

		Role userRole = roleRepository.findByName(Role.ROLE_USER);
		resource.addRole(userRole);
		resource = super.create(resource);

		// init credentials if empty
		if (credentials == null) {
			credentials = new UserCredentials();
		}

		credentials.setUser(resource);
		if (credentials.getActive() && credentials.getPassword() == null) {
			throw new BadRequestException("User to create is active but has no password set");
		}

		credentials = this.credentialsRepository.save(credentials);
		
		resource.setCredentials(credentials);
		
		return resource;
	}

	@Override
	@Transactional(readOnly = false)
	public User handlePasswordResetToken(String userNameOrEmail, String token, String newPassword) {
		Assert.notNull(userNameOrEmail);
		User user = this.findOneByUserNameOrEmail(userNameOrEmail);
		if (user == null) {
			throw new UsernameNotFoundException("Could not match username: " + userNameOrEmail);
		}
		UserCredentials credentials = user.getCredentials();
		if (credentials == null) {
			credentials = new UserCredentials();
			credentials.setUser(user);
		}
		credentials.setResetPasswordToken(null);
		credentials.setPassword(newPassword);
		credentials = this.credentialsRepository.save(credentials);

		LOGGER.info("handlePasswordResetToken returning local user: " + user);
		return user;
	}

	@Override
	@Transactional(readOnly = false)
	public void handlePasswordResetRequest(String userNameOrEmail) {
		Assert.notNull(userNameOrEmail);
		User user = this.findActiveByUserNameOrEmail(userNameOrEmail);
		if (user == null) {
			throw new UsernameNotFoundException("Could not match username/email to an active user: " + userNameOrEmail);
		}
		// keep any existing token
		if(user.getCredentials().getResetPasswordToken() == null){
			user.getCredentials().setResetPasswordToken(this.generator.generateKey());
			user = this.userRepository.save(user);
		}
		emailService.sendPasswordResetLink(user);
		
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
	public User findOneByUserNameOrEmail(String... tokens) {
		User found = null;
		if (tokens != null) {
			String userNameOrEmail;
			for (int i = 0; found == null && i < tokens.length; i++) {
				userNameOrEmail = tokens[i];
				if (StringUtils.isNotBlank(userNameOrEmail)) {
					found = userNameOrEmail.contains("@")
							? this.repository.findByEmail(userNameOrEmail)
							: this.repository.findByUsername(userNameOrEmail);
				}
			}
		}
		return found;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public User findActiveByCredentials(String userNameOrEmail, String password) {
		User found = null;
		if (StringUtils.isNotBlank(userNameOrEmail) && StringUtils.isNotBlank(password)) {
			found = userNameOrEmail.contains("@")
					? this.repository.findActiveByEmailAndPassword(userNameOrEmail, password)
					: this.repository.findActiveByUsernameAndPassword(userNameOrEmail, password);
		}
		return found;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public User findActiveByUsername(String username) {
		return this.repository.findActiveByUsername(username);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public User findActiveByEmail(String email) {
		return this.repository.findActiveByEmail(email);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public User findActiveById(String id) {
		return this.repository.findActiveById(id);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public User findActiveByUserNameOrEmail(String userNameOrEmail) {
		User found = null;
		if (StringUtils.isNotBlank(userNameOrEmail)) {
			found = userNameOrEmail.contains("@")
					? this.findActiveByEmail(userNameOrEmail)
					: this.findActiveByUsername(userNameOrEmail);
		}
		return found;
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = false)
	public User createForImplicitSignup(User userAccountData) throws DuplicateEmailException {
		LOGGER.info("createForImplicitSignup, localUser: " + userAccountData);
		
		
		User existing = this.getPrincipalLocalUser();
		if (existing == null) {
			existing = this.repository.findByEmail(userAccountData.getEmail());
		}
		if (existing == null) {
			String email = userAccountData.getCredentials() != null ? userAccountData.getCredentials().getUsername() : null;
			if (StringUtils.isNotBlank(email) && email.contains("@")) {
				existing = this.repository.findByEmail(email);
			}
		}
		if (existing == null) {
			if (userAccountData.getCredentials() == null) {
				userAccountData.setCredentials(new UserCredentials());
			}

			userAccountData.getCredentials().setPassword(this.generator.generateKey());
			userAccountData.getCredentials().setActive(true);
			existing = this.createTest(userAccountData);
		}
		
		return existing;
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
		User u = this.findActiveByCredentials(userNameOrEmail, oldPassword);
		Assert.notNull(u, "Failed updating user pass: A user could not be found with the given credentials");
		
		// update password and return user
		u.getCredentials().setPassword(newPassword);
		u.getCredentials().setLastPassWordChangeDate(new Date());
		u = this.update(u);
		return u;
	}

	@Override
	@Transactional(readOnly = false)
	@PreAuthorize("hasRole('ROLE_USER')")
	public UserInvitationResultsDTO inviteUsers(UserInvitationsDTO invitations) {
		UserInvitationResultsDTO results = new UserInvitationResultsDTO();
		// add from list
		if(!CollectionUtils.isEmpty(invitations.getRecepients())){
			// Proces recepients
			for(UserDTO dto : invitations.getRecepients()){
				User u = this.repository.findByUsernameOrEmail(dto.getEmail());
				if(u == null){
					if(results.getInvited().contains(dto.getEmail())){
						// Ignore duplicate user
						results.getDuplicate().add(dto.getEmail());
					}
					else{
						// invite user
						results.getInvited().add(this.create(dto.toUser()).getEmail());
					}
				}
				else{
					// skip existing user email
					results.getExisting().add(dto.getEmail());
				}
			}
		}
		// add from string of addresses
		if(StringUtils.isNotBlank(invitations.getAddressLines())){
			List<String> addresses = Arrays.asList(invitations.getAddressLines().replaceAll("\\r?\\n", ",").split(","));
			if(!CollectionUtils.isEmpty(addresses)){
				for(String sAddress : addresses){
					InternetAddress email = isValidEmailAddress(sAddress);
					if(email != null){
						if(results.getInvited().contains(email.getAddress())){
							// ignore duplicate email
							results.getDuplicate().add(email.getAddress());
						}
						else{
							User u = this.repository.findByUsernameOrEmail(email.getAddress());
							if(u == null){
								u = new User();
								u.setEmail(email.getAddress());
								String personal = email.getPersonal();
								if(StringUtils.isNotBlank(personal)){
									String[] names = personal.split(" ");
									if(names.length > 0){
										if(StringUtils.isNotBlank(names[0])){
											u.setFirstName(names[0]);
										}
										if(names.length > 1 && StringUtils.isNotBlank(names[1])){
											u.setLastName(names[1]);
										}
										// handle middle name
										if(names.length > 2 && StringUtils.isNotBlank(names[2])){
											u.setLastName(names[2]);
										}
									}
								}
								// invite user
								results.getInvited().add(this.create(u).getEmail());
							}
						}
					}
					else{
						// ignore invalid email
						results.getInvalid().add(sAddress);
					}
				}
			}
			
		}

		return results;
		
	}
	
	public static InternetAddress isValidEmailAddress(String email) {
		InternetAddress emailAddr = null;
		   try {
		      emailAddr = new InternetAddress(email);
		      emailAddr.validate();
		   } catch (AddressException ex) {
			   emailAddr = null;
		   }
		   return emailAddr;
		}
}