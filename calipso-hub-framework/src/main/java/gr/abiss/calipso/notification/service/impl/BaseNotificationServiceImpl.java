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
package gr.abiss.calipso.notification.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import gr.abiss.calipso.model.User;
import gr.abiss.calipso.notification.model.BaseNotification;
import gr.abiss.calipso.notification.service.BaseNotificationService;
import gr.abiss.calipso.notification.repository.BaseNotificationRepository;
import gr.abiss.calipso.tiers.service.AbstractModelServiceImpl;
import gr.abiss.calipso.userDetails.integration.LocalUser;
import gr.abiss.calipso.userDetails.model.ICalipsoUserDetails;
import gr.abiss.calipso.web.spring.ParameterMapBackedPageRequest;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Named("baseNotificationService")
public class BaseNotificationServiceImpl extends AbstractModelServiceImpl<BaseNotification, String, BaseNotificationRepository> 
	implements BaseNotificationService{

	private static final Logger LOGGER = LoggerFactory.getLogger(BaseNotificationServiceImpl.class);

	private static final String RECEPIENT = "recepient";

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Inject
	public void setRepository(BaseNotificationRepository baseNotificationRepository) {
		super.setRepository(baseNotificationRepository);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Page<BaseNotification> findAll(Pageable pageable) {

		ParameterMapBackedPageRequest mapBackedPageable = (ParameterMapBackedPageRequest) pageable;

		ICalipsoUserDetails principal = this.getPrincipal();
		
		// if no exlicit recepient is given, use the current principal
		if(principal != null){

			Map<String, String[]> paramsMapOrig = mapBackedPageable.getParameterMap();
			
			Map<String, String[]> paramsMap = new HashMap<String, String[]>();
			paramsMap.putAll(paramsMapOrig);
			String[] ids = {principal.getId()};
			paramsMap.put(RECEPIENT, ids);
			mapBackedPageable.setParameterMap(paramsMap);
		}
	
		return new PageImpl<BaseNotification>(new ArrayList<BaseNotification>(0), pageable, new Long(0));//super.findAll(pageable);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Long countUnseen() {
        return this.countUnseen(this.getPrincipal());
    }

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Long countUnseen(User recepient) {
        return repository.countUnseen(recepient.getId());
    }

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Long countUnseen(LocalUser recepient) {
        return repository.countUnseen(recepient.getId());
    }

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Long countUnseen(ICalipsoUserDetails recepient) {
        return repository.countUnseen(recepient.getId());
    }
		
	
	
	

}