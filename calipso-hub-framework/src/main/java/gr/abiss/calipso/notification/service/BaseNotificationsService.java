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
package gr.abiss.calipso.notification.service;

import java.io.Serializable;

import gr.abiss.calipso.jpasearch.service.GenericService;
import gr.abiss.calipso.model.User;
import gr.abiss.calipso.notification.model.BaseNotification;
import gr.abiss.calipso.userDetails.integration.LocalUser;
import gr.abiss.calipso.userDetails.model.ICalipsoUserDetails;
import gr.abiss.calipso.userDetails.model.UserDetails;

public interface BaseNotificationsService extends GenericService<BaseNotification, String> {

	/**
	 * Count the unseen notifications addressed to the current principal
	 * @return the count
	 */
	Long countUnseen();

	/**
	 * Count the unseen notifications addressed to the given user 
	 * @return the count
	 */	
	Long countUnseen(User recepient);
	
	/**
	 * Count the unseen notifications addressed to the given user 
	 * @return the count
	 */	
	Long countUnseen(LocalUser recepient);
	
	/**
	 * Count the unseen notifications addressed to the given principal
	 * @return the count
	 */
	Long countUnseen(ICalipsoUserDetails recepient);


}