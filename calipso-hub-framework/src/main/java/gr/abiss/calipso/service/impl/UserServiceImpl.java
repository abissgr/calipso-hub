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

import gr.abiss.calipso.model.Role;
import gr.abiss.calipso.model.User;
import gr.abiss.calipso.model.UserDTO;
import gr.abiss.calipso.model.interfaces.Metadatum;
import gr.abiss.calipso.model.metadata.UserMetadatum;
import gr.abiss.calipso.repository.RoleRepository;
import gr.abiss.calipso.repository.UserRepository;
import gr.abiss.calipso.service.EmailService;
import gr.abiss.calipso.service.UserService;
import gr.abiss.calipso.tiers.service.AbstractModelServiceImpl;
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
import javax.persistence.Query;

import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.LockMode;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.StatelessSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.security.crypto.keygen.StringKeyGenerator;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

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
		Role userRole = roleRepository.findByName(Role.ROLE_USER);
		resource.addRole(userRole);
		resource.setResetPasswordToken(generator.generateKey());
		resource = super.create(resource);
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
        org.hibernate.Query query = session.createQuery("SELECT new gr.abiss.calipso.model.UserDTO(u.id, u.firstName, u.lastName,u.username, u.email, u.emailHash) FROM User u "
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


}