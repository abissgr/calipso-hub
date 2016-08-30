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
package gr.abiss.calipso.tiers.service;

import gr.abiss.calipso.model.interfaces.CalipsoPersistable;
import gr.abiss.calipso.repository.UserRepository;
import gr.abiss.calipso.service.EmailService;
import gr.abiss.calipso.tiers.repository.ModelRepository;
import gr.abiss.calipso.tiers.service.impl.AbstractAclAwareServiceImpl;
import gr.abiss.calipso.userDetails.integration.LocalUser;
import gr.abiss.calipso.userDetails.model.ICalipsoUserDetails;
import gr.abiss.calipso.userDetails.util.SecurityUtil;

import java.io.Serializable;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Persistable;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.security.crypto.keygen.StringKeyGenerator;


public abstract class AbstractModelServiceImpl<T extends CalipsoPersistable<ID>, ID extends Serializable, R extends ModelRepository<T, ID>>
		extends AbstractAclAwareServiceImpl<T, ID, R> 
implements ModelService<T, ID>{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractModelServiceImpl.class);
	
	private final StringKeyGenerator generator = KeyGenerators.string();
	
	protected UserRepository userRepository;
	protected EmailService emailService;
	
	@Inject
	protected SimpMessageSendingOperations messagingTemplate;
	
	@Override
	@Inject
	public void setRepository(R repository) {
		LOGGER.debug("setRepository: " + repository);
		super.setRepository(repository);
	}
	
	@Inject
	public void setEmailService(EmailService emailService) {
		this.emailService = emailService;
	}
	
	@Inject
	public void setUserRepository(UserRepository userRepository) {
		this.userRepository = userRepository;
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
				user = this.userRepository.findByUsernameOrEmail(username);
			}
		}

		if(LOGGER.isDebugEnabled()){
			LOGGER.debug("getPrincipalUser, user: " + user);
		}
		return user;
	}

}