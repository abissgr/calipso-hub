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

import gr.abiss.calipso.jpasearch.repository.BaseRepository;
import gr.abiss.calipso.jpasearch.service.impl.GenericServiceImpl;
import gr.abiss.calipso.model.User;
import gr.abiss.calipso.model.dto.ReportDataSet;
import gr.abiss.calipso.model.types.AggregateFunction;
import gr.abiss.calipso.model.types.TimeUnit;
import gr.abiss.calipso.repository.UserRepository;
import gr.abiss.calipso.service.EmailService;
import gr.abiss.calipso.userDetails.integration.LocalUser;
import gr.abiss.calipso.userDetails.model.ICalipsoUserDetails;
import gr.abiss.calipso.userDetails.util.SecurityUtil;

import java.io.Serializable;
import java.util.Date;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Persistable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.security.crypto.keygen.StringKeyGenerator;
import org.springframework.web.bind.annotation.RequestParam;


public abstract class AbstractServiceImpl<T extends Persistable<ID>, ID extends Serializable, R extends BaseRepository<T, ID>>
		extends
 GenericServiceImpl<T, ID, R> {
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractServiceImpl.class);
	
	private final StringKeyGenerator generator = KeyGenerators.string();
	
	protected UserRepository userRepository;
	protected EmailService emailService;

	@Inject
	public void setEmailService(EmailService emailService) {
		this.emailService = emailService;
	}
	
	@Inject
	public void setUserRepository(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public ICalipsoUserDetails getPrincipal() {
		return SecurityUtil.getPrincipal();
	}

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