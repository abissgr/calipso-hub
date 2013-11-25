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
import gr.abiss.calipso.repository.UserRepository;

import java.io.Serializable;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Persistable;
import org.springframework.security.core.context.SecurityContextHolder;


public abstract class AbstractServiceImpl<T extends Persistable<ID>, ID extends Serializable, R extends BaseRepository<T, ID>>
		extends
 GenericServiceImpl<T, ID, R> {
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractServiceImpl.class);


	protected UserRepository userRepository;

	@Inject
	public void setUserRepository(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
	protected User getPrincipal() {
		Object principal = null;
		if (SecurityContextHolder.getContext() != null && SecurityContextHolder.getContext().getAuthentication() != null) {
			principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		}
		User user = null;
		if(principal != null 
				&& principal instanceof org.springframework.security.core.userdetails.User){
			String username = ((org.springframework.security.core.userdetails.User) principal).getUsername();
			if(StringUtils.isNotBlank(username) && !"anonymous".equals(username)){
				user = userRepository.findByUserNameOrEmail(username);
			}
		}
		return user;
	}

}