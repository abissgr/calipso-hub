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
package gr.abiss.calipso.service.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.LockMode;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.StatelessSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.security.crypto.keygen.StringKeyGenerator;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import gr.abiss.calipso.model.Role;
import gr.abiss.calipso.model.User;
import gr.abiss.calipso.model.dto.UserDTO;
import gr.abiss.calipso.model.dto.UserInvitationResultsDTO;
import gr.abiss.calipso.model.dto.UserInvitationsDTO;
import gr.abiss.calipso.model.interfaces.Metadatum;
import gr.abiss.calipso.model.metadata.UserMetadatum;
import gr.abiss.calipso.repository.RoleRepository;
import gr.abiss.calipso.repository.UserRepository;
import gr.abiss.calipso.service.UserService;
import gr.abiss.calipso.tiers.service.AbstractModelServiceImpl;
import gr.abiss.calipso.userDetails.integration.LocalUser;
import gr.abiss.calipso.userDetails.model.ICalipsoUserDetails;
import gr.abiss.calipso.userDetails.util.DuplicateEmailException;

//@Named("userService")
public class UserServiceImpl extends AbstractModelServiceImpl<User, String, UserRepository> 
	implements UserService {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

	private final StringKeyGenerator generator = KeyGenerators.string();
	private RoleRepository roleRepository;

	@Inject
	public void setRoleRepository(RoleRepository roleRepository) {
		this.roleRepository = roleRepository;
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
		LOGGER.debug("create, PRE_AUTHORIZE_CREATE: {}", resource.PRE_AUTHORIZE_CREATE);
		Role userRole = roleRepository.findByName(Role.ROLE_USER);
		resource.addRole(userRole);
		resource.setResetPasswordToken(generator.generateKey());
		LOGGER.debug("create, PRE_AUTHORIZE_CREATE1: {}", resource.PRE_AUTHORIZE_CREATE);
		resource = super.create(resource);
		LOGGER.debug("create, PRE_AUTHORIZE_CREATE2: {}", resource.PRE_AUTHORIZE_CREATE);
		emailService.sendAccountConfirmation(resource);
		LOGGER.debug("create, PRE_AUTHORIZE_CREATE3: {}", resource.PRE_AUTHORIZE_CREATE);
		return resource;
	}


	@Override
	@Transactional(readOnly = false)
	public void expireResetPasswordTokens() {
		// get a hibernate session suitable for read-only access to large datasets
		StatelessSession session = ((Session) this.repository.getEntityManager().getDelegate()).getSessionFactory().openStatelessSession();
		Date yesterday = DateUtils.addDays(new Date(), -1);
		
		// send email notifications for account confirmation tokens that expired
        org.hibernate.Query query = session.createQuery("SELECT new gr.abiss.calipso.model.UserDTO(u.id, u.firstName, u.lastName,u.username, u.email, u.emailHash, u.avatarUrl) FROM User u "
        		+ "WHERE u.password IS NULL and u.resetPasswordTokenCreated IS NOT NULL and u.resetPasswordTokenCreated  < :yesterday");
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
	public User createActive(User resource) {
		Role userRole = roleRepository.findByName(Role.ROLE_USER);
		resource.addRole(userRole);
		resource.setResetPasswordToken(generator.generateKey());
		resource = super.create(resource);
		return resource;
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